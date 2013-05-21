package skitch

import common.clamp
import collection.mutable
import org.lwjgl.opengl.GL11._
import org.newdawn.slick

object Color {

  var store = mutable.Map[Int, Color]()

  def tuple2hex(r: Float, g: Float, b: Float, a: Float = 1.0f): Int = {
    ((a * 255).toInt << 6) +
      ((r * 255).toInt << 4) +
      ((g * 255).toInt << 2) +
      ((b * 255).toInt)
  }

  //      def apply(r:Int, g:Int, b:Int) = {
  //         store.getOrElseUpdate(tuple2hex(r,g,b), new Color(r.toFloat/255f, g.toFloat/255f, b.toFloat/255f, 1.0f))
  //      }
  def apply(r: Float, g: Float, b: Float, a: Float = 1.0f) = {
    store.getOrElseUpdate(tuple2hex(r, g, b, a), new Color(r, g, b, a))
  }

  def apply(h: Int) = {
    new Color(((h & 0xff0000) >> 16) / 255f, ((h & 0xff00) >> 8) / 255f, ((h & 0xff)) / 255f, 1f)
  }
  def apply(h: Int, a:Float) = {
    new Color(((h & 0xff0000) >> 16) / 255f, ((h & 0xff00) >> 8) / 255f, ((h & 0xff)) / 255f, a)
  }

  def hsv(h:Float, s:Float, v:Float, a:Float=1.0f) = {
    Color( java.awt.Color.HSBtoRGB(h,s,v), a )
  }

  def lerp(c1: Color, c2: Color, amt: Float): Color = {
    val k = clamp(amt)(0, 1)
    c1 * (1 - k) + c2 * k
  }

  def alerp(c1: Color, c2: Color, amt: Float): Color = {
    val k = clamp(amt)(0, 1)
    c1 * (1 - k) + c2 * k
  }

  private val h = 0.5f
  val white = Color(1, 1, 1)
  val gray = Color(0.5f, 0.5f, 0.5f)
  val black = Color(0, 0, 0)

  val cyan = Color(0, 1, 1)
  val magenta = Color(1, 0, 1)
  val yellow = Color(1, 1, 0)
  val red = Color(1, 0, 0)
  val green = Color(0, 1, 0)
  val blue = Color(0, 0, 1)

  val purple = Color(h, 0, h)
  val orange = Color(1, h, 0)

}

class Color(var r: Float, var g: Float, var b: Float, var a: Float = 1.0f) {

  @inline
  def bind() {
    glColor4f(this.r, this.g, this.b, this.a)
  }

  @inline
  def apply() {
    bind()
  }

  override def toString = "[Color (r=%f, g=%f, b=%f, a=%f)]".format(r, g, b, a)

  def toAWT = new java.awt.Color(r, g, b, a)

  def toSlick = new slick.Color(r, g, b, a)

  def inverted = new Color(1 - r, 1 - g, 1 - b, a)

  def alpha(A:Float) = { new Color(r,g,b,A) }

  def value = (r + g + b) / 3

  //NOTE: what to do about alpha values?
  def +(o: Color) = new Color(r + o.r, g + o.g, b + o.b, (a + o.a) / 2)

  def -(o: Color) = new Color(r - o.r, g - o.g, b - o.b, (a + o.a) / 2)

  def *(v: Float) = new Color(clamp(r * v)(0, 1), clamp(g * v)(0, 1), clamp(b * v)(0, 1), a)

  def **(v: Float) = new Color(1 - r / v, 1 - g / v, 1 - b / v, a)

  def /(v: Float) = new Color(r / v, g / v, b / v, a)
}
