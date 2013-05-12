package skitch.gfx

import java.io.{FileInputStream, File}
import skitch.core.{Resource, Tex, Render}
import skitch.helpers.FileLocation


trait ImageLike extends SubTexture with Render{
  def clip:ClipRect
  def width:Int
  def height:Int
}

case class Image(val tex:Tex, cliprect:Option[ClipRect]) extends ImageLike {

  lazy val clip = cliprect.getOrElse(ClipRect(0,0,texWidth,texHeight))
  val (width, height) = (clip.w, clip.h)
  def render() { blit() }
  override def toString = "Image(tex=%s, clip=%s)".format(tex, clip)
}

object Image {

	def load(loc:FileLocation, clip:ClipRect) = {
		new Image(Tex.load(loc), Some(clip))
	}

	def load(loc:FileLocation) = new Image(Tex.load(loc), None)

}

