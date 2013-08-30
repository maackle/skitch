package skitch.gfx

import scala.Array
import org.lwjgl.opengl.GL11._
import skitch.core.Textured
import skitch.vector.vec
import skitch.{Color, gl}

//TODO: consolidate with Rect
case class ClipRect(x:Int, y:Int, w:Int, h:Int)

trait SubTexture extends Textured {
  //  protected def texture:Texture
  def clip:ClipRect

  lazy protected val (texWidth, texHeight) = (tex.getImageWidth, tex.getImageHeight)
  lazy protected val (paddedWidth, paddedHeight) = (tex.getTextureWidth.toFloat, tex.getTextureHeight.toFloat)
  lazy protected val (tw, th) = (clip.w / paddedWidth, clip.h / paddedHeight )
  lazy private val (tx0, ty0) = (clip.x / paddedWidth, clip.y / paddedHeight)
  lazy private val (tx1, ty1) = (tx0 + tw, ty0 + th)
//  lazy private val (vx0, vy0) = (clip.x, clip.y)
//  lazy private val (vx1, vy1) = (clip.x + clip.w, clip.y + clip.h)
  lazy private val (vx0, vy0) = (0, 0)
  lazy private val (vx1, vy1) = (clip.w, clip.h)

  lazy val texCoords = Array(
    vec(tx0, ty0),
    vec(tx1, ty0),
    vec(tx1, ty1),
    vec(tx0, ty1)
  ).reverse
  lazy val vertices = Array(
    vec(vx0, vy0),
    vec(vx1, vy0),
    vec(vx1, vy1),
    vec(vx0, vy1)
  )

  def blit() {

    bindAnd {
//      vbo.draw(GL_TRIANGLE_FAN)
      oldWay
    }
    def oldWay = {
      gl.begin(GL_TRIANGLE_FAN) {
        glTexCoord2f(tx0, ty1)
        glVertex2f(vx0, vy0)

        glTexCoord2f(tx1, ty1)
        glVertex2f(vx1, vy0)

        glTexCoord2f(tx1, ty0)
        glVertex2f(vx1, vy1)

        glTexCoord2f(tx0, ty0)
        glVertex2f(vx0, vy1)
      }
    }
  }
}
