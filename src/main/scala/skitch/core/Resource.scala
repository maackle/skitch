package skitch.core

import java.io.{PrintWriter, IOException, FileInputStream, File}
import java.nio.file.{Path, WatchEvent, WatchKey, FileSystems}
import skitch.common
import java.lang.InterruptedException
import scala.collection.JavaConversions
import JavaConversions._
import grizzled.slf4j.Logging
import skitch.helpers.FileLocation
import skitch.gfx.Image

sealed trait ResourceLike {

	private[core] var dependents:Set[ResourceDependency] = Set.empty

}

trait ResourceDependency extends ResourceLike {

//	def addDependency(reso:Resource[_]) {
//		reso
//	}
	val resourceDependencies:Set[ResourceLike]

	private[core] def __refresh(changed:ResourceLike) {
		initialize
		refresh(changed)
	}
	protected def refresh(changed:ResourceLike)

	lazy val initialize = {
		for(reso <- resourceDependencies)
			reso.dependents += this
	}

}

trait ImageResource extends Resource[Image] {
  val loadFn = (loc:FileLocation) => Image.load(loc)
  override def refresh() = {
//    is.tex.slickTexture.release()
    super.refresh()
  }
}

trait Resource[T <: Resource.Bound] extends ResourceLike with Logging {
  import Resource._
  import ResourceLoader._

	type DepSet = Set[ResourceDependency]

  private var x:T = null.asInstanceOf[T]

  protected val loader: ResourceLoader
	protected val loadFn: Loader[T]

  val location: FileLocation

	def map[A <: Bound](fn:(T=>A)) = {
		loader.apply[A](location)(loc => {
			fn(loadFn(loc))
		})
	}

	def is = {
		if (! isLoaded) throw ResourceAccessException
		x
	}

	def option = Option( is )

	def isLoaded = { x != null }

	private[core] def load() = {
		//TODO: exception for IO error
		x = loadFn(location)
	}

	private[core] def refresh() = {
//    SkitchApp.SlickTextureLoader.clear()
		load()
		info("resource refreshed: %s" format location.file)
		type R = ResourceLike
		type DS = Set[ResourceDependency]
		def traverse(relationship:(DS, R)):Seq[(DS, R)] = {
			relationship match {
				case (dependents:DS, res:R) =>
					dependents.toSeq.flatMap(r => traverse((r.dependents, res)))
				case _ => Seq()
			}
		}

		for {
			(list, reso) <- traverse((dependents, this))
			dep <- list
		} {
			dep.__refresh(reso)
		}
	}

	object ResourceAccessException extends Exception("tried to access resource at '%s' before loading" format location.file)

}

object Resource extends Logging {

  type Bound = AnyRef

}
