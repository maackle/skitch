package skitch.gfx


import org.newdawn.slick.UnicodeFont
import org.newdawn.slick.font.effects.ColorEffect
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL11._
import grizzled.slf4j.Logging
import java.awt
import skitch.core.{Tex, Java, Resource}
import skitch.{common, Color, gl}
import skitch.vector.{vec2, vec}
import java.io.{File, FileInputStream}


class Font(path:String, size:Int, style:Font.Style.Plain.type) {

  var anchor = vec2.zero

  //TODO: resource-ify this
  lazy val uni = {
    val u = new UnicodeFont(path, size, false, false)
    u.addNeheGlyphs()
    //   uni.addGlyphs(0x21ba, 0x21bb)
    Java.addEffect(u, (new ColorEffect(java.awt.Color.WHITE)))
    u.loadGlyphs()
    u
  }

  //TODO: offsets
  def drawString(what:String, pos:vec2, color:Color, anchor:vec2=anchor, scale:Float=1f) {
    skitch.gl.fill(true)
    val w = uni.getWidth(what)
    val h = uni.getHeight(what)
    val x = pos.x - (anchor.x/2 + .5f) * w
    val y = pos.y - (anchor.y/2 + .5f) * h
    val offset = vec(
      (anchor.x/2 + .5f) * -w,
      (anchor.y/2 + .5f) * -h
    )

    Tex.bindNone()

    skitch.gl.matrix {
      skitch.gl.translate(pos)
      skitch.gl.scale(scale, scale)
      skitch.gl.translate(offset)
      gl.scale(1,-1)
      uni.drawString(0, 0, what, color.toSlick )
    }
    GL11.glDisable(GL_TEXTURE_2D) // TODO: why is this necessary? (it is, don't remove until you understand!)
  }

}

object Font extends Logging {

  import Resource._

//  lazy val default = Resource("font/UbuntuMono-R.ttf")(Font.load(_, 20)).is
  lazy val default = (Font.load(common.getFile("font/UbuntuMono-R.ttf"), 20))

  def load(fis:File, size:Float, style:Style.Value=Style.Plain) = {
    val base = awt.Font.createFont(awt.Font.TRUETYPE_FONT, fis)
    val font = base.deriveFont(style.id, size)
    info("loaded font: " + font)
    font
  }

  object Style extends Enumeration {
    val Plain = Value(0)
    val Bold = Value(1)
    val Italic = Value(2)
    val BoldItalic = Value(4)
  }

  //TODO -- perhaps use case classes instead, with a method to modify a position given width and height
  object Anchor extends Enumeration {
    val Center, TopLeft, BottomLeft, TopRight, BottomRight = Value
  }
}
