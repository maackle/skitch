package skitch.core

import grizzled.slf4j.Logging
import java.io.{IOException, FileInputStream, File}
import skitch.common
import java.nio.file.{Path, WatchEvent, WatchKey, FileSystems}
import java.lang.InterruptedException
import scala.collection.JavaConversions
import JavaConversions._
import skitch.helpers.FileLocation
import skitch.gfx.Image


object ResourceLoader {
  type Loader[T <: Resource.Bound] = FileLocation => T
  type Res = Resource[ _ <: Resource.Bound]

}

class ResourceLoader(baseDirectory:File)(implicit app:SkitchApp) extends Logging { self =>

  import Resource._
  import ResourceLoader._

  case class Location(path:String) extends FileLocation(getFile(path)) {
    lazy override val toString = fullPath
  }

  implicit def string2location(path:String) = Location(path)

  private var autoload_? = false
  private val devmode_? = true  // TODO: allow this to change
  private var unloaded = Set[Res]()
  private var pendingRefresh = Set[Res]()
  private var loaded = Map[String, Res]()

  app._resourceLoaders += this
//
//  def apply[T <: Bound](locator:String)(loader: Loader[T]):Resource[T] = {
//    apply(Location(locator))(loader)
//  }

  class ResourceImpl[T <: Bound](val location:FileLocation, val loadFn:Loader[T], val loader:ResourceLoader) extends Resource[T] {

  }

  private def register(reso:Res) {

    if(autoload_?) {
      reso.load()
    } else {
      unloaded += reso
    }
  }

  def apply[T <: Bound](path:String)(loadFn: Loader[T]):Resource[T] = apply(Location(path))(loadFn)

  def apply[T <: Bound](locator:FileLocation)(loadFn: Loader[T]):Resource[T] = {
    val loc = locator
    val _loadFn = loadFn
    val reso = new ResourceImpl[T] (
      loader = this,
      location = loc,
      loadFn = _loadFn
    )

    register(reso)

    reso
  }

  def image(path:String):ImageResource = {

    val reso = new ImageResource {
      val location = Location(path)
      val loader = self
    }
    register(reso)
    reso
  }

  def autoload() {
    autoload_? = true
    unloaded.toSeq.foreach { r =>
      r.load()
      unloaded -= r
      loaded += (r.location.fullPath -> r)
    }
  }

  def getFile(path:String) = {
    if(devmode_?) {
      new File(baseDirectory, path)
    }
    else {
      common.getFile(path)
    }
  }

  def getInputStream(path:String) = {
    new FileInputStream( getFile(path) )
  }

  implicit def path2stream(path:String) = getInputStream(path)

  private var watchedDirectories = Set[File](baseDirectory)

  def watch() {

    watchThread.start

    if (watchThread.isAlive) {
      debug("watch service is running")
    } else {
      error("couldn't start watch service")
    }
  }

  def update() {
    for (reso <- pendingRefresh.toSeq; file = reso.location.file if file.isFile && file.length > 0) {
      reso.refresh()
      pendingRefresh -= reso
    }
  }

  protected[skitch] lazy val watchThread = new Thread (

    new Runnable {

      def run() {

        import java.nio.file.StandardWatchEventKinds._

        val watcher = FileSystems.getDefault().newWatchService()

        val watched = collection.mutable.Map[WatchKey, File]()

        def subdirectories(dir:File):Array[File] = {
          if (! dir.isDirectory) {
            Array()
          } else {
            val these = dir.listFiles().filter(_.isDirectory)
            these ++ these.flatMap(subdirectories)
          }
        }

        def watchDir(dir:File) {
          try {
            val key = dir.toPath.register(watcher, ENTRY_MODIFY);
            info("watching directory: %s" format dir)
            watched(key) = dir
          } catch {
            case e:IOException =>
              error("failed to watch directory: %s" format dir)
          }
        }

        for (dir <- watchedDirectories) {

          watchDir(dir)

          for (sub <- subdirectories(dir)) {
            watchDir(sub)
          }
        }

        while(true) {

          val key =  try {
            watcher.take()
          } catch {
            case e:InterruptedException =>
              info("interrupted")
              return
          }

          for {
            event <- key.pollEvents()
            dir <- watched.get(key)
            kind = event.kind
          } {
            if(kind != OVERFLOW) {
              val filename = event.asInstanceOf[WatchEvent[Path]].context()
              val path = dir.toPath.resolve(filename)
              loaded.get(path.toString).map({ reso =>
                info("refreshing: " + path)
                pendingRefresh += reso
              }).orElse {
                warn("file change detected, but no match: %s" format path)
                println(loaded)
                None
              }
            }
          }

          val valid = key.reset();
          if (!valid) {
            return
          }

        }

      }
    }
  )

}
