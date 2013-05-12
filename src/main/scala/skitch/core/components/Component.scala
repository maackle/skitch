package skitch.core.components

trait Component {

}

import skitch.vector.vec
import skitch.vector.vec2
import skitch.common
import skitch.core.Update
import skitch.gfx

trait Position extends Component { def position: vec }
trait Position2D extends Component {
  def position: vec2
}

trait PositionXY extends Position2D {
  def x: common.Real
  def y: common.Real
  def x_=(x:common.Real)
  def y_=(y:common.Real)
  //  var x, y : common.Real
  def position = vec(x,y)
  def position_=(v:vec2) {
    x = v.x
    y = v.y
  }
}

trait Rotation {
  def rotation: common.Radian
}

trait Scaling2D {
  def scale: vec2
}

trait Velocity extends Component { def velocity: vec }
trait Velocity2D extends Component with Position2D {
  def velocity: vec2
}

trait Acceleration extends Component { def acceleration: vec }
trait Acceleration2D extends Component with Velocity2D {
  def acceleration: vec2
}

trait Verlet2D extends Acceleration2D with Update {

  private[skitch] override def __update(dt:common.Real) {
    super.__update(dt)
    position += velocity * dt
    velocity += acceleration * dt
  }
}

//trait Shape extends Position {
//  def hitTest(other:Shape):Boolean
//  def hitTest(point:vec):Boolean
//}
trait Shape2D extends Position2D {
  def hitTest(other:Shape2D):Boolean
  def hitTest(other:vec2):Boolean
}

trait CircleShape extends Shape2D {

  def radius:common.Real

  def hitTest(point:vec2) = {
    (point - position).lengthSquared < radius * radius
  }

  def hitTest(other:Shape2D) = other match {
    case circle:CircleShape =>
      val radii = radius + circle.radius
      (position - circle.position).lengthSquared < radii * radii
    case rect:RectangleShape =>
      ???
  }

  def draw() {
    gfx.circle(radius)
  }
}

trait ConvexPolygonShape extends Shape2D {
  def vertices:Seq[vec2] = ???
}

object RectangleShape {
  def apply(a:vec2, b:vec2) = new RectangleShape {
    val position = (a + b) / 2
    val width = math.abs(a.x - b.x)
    val height = math.abs(a.y - b.y)
  }
}

trait RectangleShape extends ConvexPolygonShape {

  def width:skitch.common.Real
  def height:common.Real

  def halfWidth = width / 2
  def halfHeight = height / 2

  def hitTest(point:vec2) = {
    val (x0, y0) = (position - vec(halfWidth, halfHeight)).tuple
    val (x1, y1) = (position + vec(halfWidth, halfHeight)).tuple
    val (px, py) = point.tuple
    x0 <= px && px <= x1 && y0 <= py && py <= y1
  }

  def hitTest(other:Shape2D) = {
    val (x0, y0) = (position - vec(halfWidth, halfHeight)).tuple
    val (x1, y1) = (position + vec(halfWidth, halfHeight)).tuple
    other match {
      case circle:CircleShape =>
        val (cx, cy) = circle.position.tuple
        val r = circle.radius
        if(x0 <= cx && cx <= x1) {
          if(cy < y0) cy + r >= y0
          else if(cy > y1) cy - r <= y1
          else true
        }
        else if(y0 <= cy && cy <= y1) {
          if(cx < x0) cx + r >= x0
          else if(cx > x1) cx - r <= x1
          else true
        }
        else ???
      case rect:RectangleShape =>
        val tests = List(vec(x0,y0), vec(x1, y0), vec(x1, y1), vec(x0, y1)) map { v =>
          rect.hitTest(v)
        }
        tests.reduce(_ && _)
    }
  }

  def draw() {
    gfx.rect(width, height)
  }
}
