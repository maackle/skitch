package skitch.gfx

import java.io.{FileInputStream, File}
import skitch.core.{Resource, Tex, Render}
import skitch.helpers.FileLocation
import skitch.vector.{vec2, vec}
import skitch.{Color, gl}


trait ImageLike extends SubTexture with Render{
	def clip:ClipRect
	def width:Int
	def height:Int
}

case class Image(tex:Tex, origin:vec2, cliprect:Option[ClipRect]=None, var blitColor:Color=Color.white) extends ImageLike {

	lazy val clip = cliprect.getOrElse(ClipRect(0,0,texWidth,texHeight))
	val (width, height) = (clip.w, clip.h)
	val dimensions = vec(width, height)
	val center = dimensions / 2
	def render() = gl.matrix {
		gl.translate(-origin)
    blitColor.bind()
    gl.fill(true)
		blit()
	}
	override def toString = "Image(tex=%s, clip=%s)".format(tex, clip)
}

object Image {

	def load(loc:FileLocation, origin:vec2=null, clip:ClipRect=null, blitColor:Color=Color.white) = {
    val tex = Tex.load(loc)
    val o = if (origin == null) tex.dimensions / 2 else origin
		new Image(tex, o, Option(clip), blitColor)
	}

}

