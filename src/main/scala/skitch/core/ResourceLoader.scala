package skitch.core

import grizzled.slf4j.Logging
import java.io.{IOException, FileInputStream, File}
import skitch.{Color, common}
import java.nio.file.{Path, WatchEvent, WatchKey, FileSystems}
import java.lang.InterruptedException
import scala.collection.JavaConversions
import JavaConversions._
import skitch.helpers.FileLocation
import skitch.vector.vec2
import skitch.gfx.{Image, ClipRect}


object ResourceLoader {
	type Loader[T] = FileLocation => T
	type Res = Resource[_]

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
	private var notLoaded = Set[Res]()
	private var pendingRefresh = Set[Res]()
	private var loaded = Map[String, Res]()
	private var registered = Map[FileLocation, Res]()

	app._resourceLoaders += this
	//
	//  def apply[T <: Bound](locator:String)(loader: Loader[T]):Resource[T] = {
	//    apply(Location(locator))(loader)
	//  }
//
//	class ResourceImpl[T <: Bound](val location:FileLocation, val loadFn:Loader[T], val loader:ResourceLoader) extends Resource[T] {
//
//	}

	// TODO TODO TODO!!  Need way to derive a resource from another!


	private def register(reso:Res) {
		logger.debug("registering %s".format(reso.location))
		if(autoload_?) {
			reso.load()
		} else {
			notLoaded += reso
		}
		registered += (reso.location -> reso)
	}

	private[skitch] def markLoaded(reso:Res) {
		// TODO maybe this doesn't matter after autoload is invoked?
		notLoaded -= reso
		loaded += (reso.location.fullPath -> reso)
	}

	private def doIfFresh[A](location:FileLocation)(fn: =>A):A = {
		registered.get(location).map(_.asInstanceOf[A]).getOrElse {
			logger.info("FRESH! " + location)
			fn
		}
	}


	def apply[T](path:String)(loadFn: Loader[T]):Resource[T] = apply(Location(path))(loadFn)

	def apply[T](location:FileLocation)(loadFn: Loader[T]):Resource[T] = {

		doIfFresh[Resource[T]](location) {
			val loc = location
			val _loadFn = loadFn
			val reso = new PrimaryResource[T] {
				val loader = self
				val location = loc
				val loadFn = _loadFn
			}

			register(reso)

			reso
		}
	}

  def tex(path:String):TexResource = {
    doIfFresh[TexResource](Location(path)) {
      val reso = new TexResource {
        val location: FileLocation = Location(path)
        protected[core] val loader: ResourceLoader = self
      }
      register(reso)
      reso
    }
  }

	def image(path:String)(mapper:(Tex)=>Image = (tex) => Image(tex, tex.dimensions/2)):ImageResource = {
    val _mapper = mapper
		new ImageResource {
      val base:TexResource = tex(path)
      val mapper: (Tex) => Image = _mapper
    }
	}

//  def image(path:String):ImageResource = {
//    image(path)(Image(_))
//  }

	def ogg(path:String):OggResource = {
		doIfFresh[OggResource](Location(path)) {
			val reso = new OggResource {
				val location = Location(path)
				val loader = self
			}
			register(reso)
			reso
		}
	}

	/**
	 * autoload() immediately loads any registered resources that are marked 'ableToLoad' and instructs ResourceLoader
	 * to automatically load any future resources registered after this invocation
	 */
	def autoload() {
		autoload_? = true
		loadAll()
	}

	/**
	 * load all unloaded registered resources marked 'ableToLoad'
	 */
	def loadAll() {
		notLoaded.toSeq.foreach { r =>
			if (r.ableToLoad) {
				r.load()
				logger.info("autoloaded %s".format(r.toString))
			}
			else {
				logger.info("didn't autoload %s".format(r.toString))
			}
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

	class ResourceLoaderException(msg:String) extends Exception(msg)


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
