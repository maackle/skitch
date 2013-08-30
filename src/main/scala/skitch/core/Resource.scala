package skitch.core

import scala.collection.JavaConversions
import JavaConversions._
import grizzled.slf4j.Logging
import skitch.helpers.FileLocation
import skitch.gfx.Image
import org.lwjgl.opengl.Display
import skitch.audio.{AudioData, OpenALSource}
import org.lwjgl.openal.AL
import skitch.audio.AudioData
import org.newdawn.slick.opengl.Texture

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

trait TexResource extends PrimaryResource[Tex] {
  val loadFn = (loc:FileLocation) => Tex.load(loc)
  override def ableToLoad = Display.isCreated
  //	override def onRefresh(changed:ResourceLike) = {
  //		//    is.tex.slickTexture.release()
  //		super.onRefresh()
  //	}
}

trait ImageResource extends DerivedResource[Tex, Image] {

}

trait OggResource extends PrimaryResource[AudioData] {
	val loadFn = (loc:FileLocation) => OpenALSource.loadOgg(loc.stream)

	override def ableToLoad = {
		AL.isCreated
	}

}

trait PrimaryResource[A] extends Resource[A] {
	import Resource._
	import ResourceLoader._

	protected val loadFn: Loader[A]

	private[core] def load() = {
		//TODO: exception for IO error
		if(! isLoaded) {
			x = loadFn(location)
			loader.markLoaded(this)
			logger.info("loaded resource: %s".format(location))
		}
	}

	private[core] def refresh() = {
		unload()
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
}

trait DerivedResource[A, B] extends Resource[B] {

	val base:Resource[A]

  lazy val location = base.location

  lazy val loader = base.loader

	val mapper: A=>B

	def load {
		base.load()
		x = mapper(base.is)
	}

	def refresh { base.refresh() }

}

trait Resource[A] extends ResourceLike with Logging { self =>
	import Resource._
	import ResourceLoader._

	protected var x:A = null.asInstanceOf[A]

	protected[core] val loader: ResourceLoader
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

	def map[B](fn:(A => B)):Resource[B] = {
		new DerivedResource[A,B] {
			val base = self
			val mapper = fn
		}
	}

	private[core] def load()
	private[core] def unload() { x = null.asInstanceOf[A] }
	private[core] def refresh()

	def ableToLoad:Boolean = true

	def option:Option[A] = Option( is )

	def isLoaded = { x != null }

	object ResourceAccessException extends Exception("tried to access resource at '%s' before loading" format location.file)

}

object Resource extends Logging {


}
