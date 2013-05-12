package skitch.core

import skitch.vector.{vec, vec2}

trait Rect {
  protected val x0, y0, x1, y1 : Float

  override def toString = "Rect(%s, %s)".format(bottomLeft, topRight)
  def left = x0
  def right = x1
  def bottom = y0
  def top = y1

  def bottomLeft = vec(x0, y0)
  def topRight = vec(x1, y1)
  def center = vec((x1+x0)/2, (y1+y0)/2)

  def width = x1 - x0
  def height = y1 - y0
  def dimensions = (width, height)
}

object Rect {

  def apply(p0:vec2, p1:vec2) = {
    new Rect {
      val x0 = math.min(p0.x, p1.x)
      val x1 = math.max(p0.x, p1.x)
      val y0 = math.min(p0.y, p1.y)
      val y1 = math.max(p0.y, p1.y)
    }
  }

  def apply(center:vec2, width:Float, height:Float) = {
    val (c, w, h) = (center, width, height)
    new Rect {
      val vec2(x0, y0) = c - vec(w/2, h/2)
      val vec2(x1, y1) = c + vec(w/2, h/2)
    }
  }

  def apply(LEFT:Float, BOTTOM:Float, RIGHT:Float, TOP:Float) = {
    new Rect {
      val (x0, x1, y0, y1) = (LEFT, RIGHT, BOTTOM, TOP)
    }
  }
}
