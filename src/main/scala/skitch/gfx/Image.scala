package skitch.gfx

import java.io.{FileInputStream, File}
import skitch.core.{Resource, Tex, Render}
import skitch.helpers.FileLocation
import skitch.vector.{vec2, vec}


trait ImageLike extends SubTexture with Render{
	def clip:ClipRect
	def width:Int
	def height:Int
}

case class Image(tex:Tex, origin:vec2, cliprect:Option[ClipRect]) extends ImageLike {

	lazy val clip = cliprect.getOrElse(ClipRect(0,0,texWidth,texHeight))
	val (width, height) = (clip.w, clip.h)
	val dimensions = vec(width, height)
	val center = dimensions / 2
	def render() { blit() }
	override def toString = "Image(tex=%s, clip=%s)".format(tex, clip)
}

object Image {

	def load(loc:FileLocation, origin:vec2, clip:ClipRect) = {
		new Image(Tex.load(loc), origin, Some(clip))
	}

	def load(loc:FileLocation, origin:vec2) = new Image(Tex.load(loc), origin, None)

	def load(loc:FileLocation) = {
		val tex = Tex.load(loc)
		new Image(tex, tex.dimensions / 2, None)
	}

}

