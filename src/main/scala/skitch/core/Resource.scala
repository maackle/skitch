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
import org.lwjgl.opengl.Display
import skitch.audio.{AudioData, OpenALSource}
import org.lwjgl.openal.AL

sealed trait ResourceLike {

	private[core] var dependents:Set[ResourceDependent] = Set.empty

}

trait ResourceDependent extends ResourceLike {

	val resourceDependencies:Iterable[ResourceLike]

	private[core] def __refresh(changed:ResourceLike) {
		initialize
		onRefresh(changed)
	}

	protected def beforeRefresh(changed:ResourceLike) {}
	protected def onRefresh(changed:ResourceLike)

	lazy val initialize = {
		for(reso <- resourceDependencies)
			reso.dependents += this
	}

}

trait ImageResource extends Resource[Image] {
	val loadFn = (loc:FileLocation) => Image.load(loc)

	override def ableToLoad = Display.isCreated
	//	override def onRefresh(changed:ResourceLike) = {
	//		//    is.tex.slickTexture.release()
	//		super.onRefresh()
	//	}
}

trait OggResource extends Resource[AudioData] {
	val loadFn = (loc:FileLocation) => OpenALSource.loadOgg(loc.stream)

	override def ableToLoad = {
		AL.isCreated
	}

}

trait Resource[A <: Resource.Bound] extends ResourceLike with Logging {
	import Resource._
	import ResourceLoader._

	private var x:A = null.asInstanceOf[A]

	protected[core] val loader: ResourceLoader
	protected val loadFn: Loader[A]

	val location: FileLocation

	def is:A = {
		if (! isLoaded) {
			if (ableToLoad) {
				load()
			} else {
				throw ResourceAccessException
			}
		}
		x
	}


//	def derive[B <: Resource.Bound](fn:(A => B)):Resource[B] = {
//		new loader.ResourceDerivative(this)(fn)
//	}

	def ableToLoad:Boolean = true

	def option:Option[A] = Option( is )

	def isLoaded = { x != null }

	private[core] def load() = {
		//TODO: exception for IO error
		x = loadFn(location)
		loader.markLoaded(this)
		logger.info("loaded resource: %s".format(location))
	}

	private[core] def refresh() = {
		load()
		info("resource refreshed: %s" format location.file)

		type DS = Set[ResourceDependent]
		type R = ResourceLike
		def traverse(relationship:(DS, ResourceLike)):Seq[(DS, ResourceLike)] = {
			relationship match {
				case (dependents:DS, res:ResourceLike) =>
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
