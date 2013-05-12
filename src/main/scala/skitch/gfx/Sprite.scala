package skitch.gfx


import skitch.{common, helpers, gl}
import skitch.vector.{vec2, vec}
import skitch.{helpers, gl}
import skitch.core.Render
import skitch.core.components.Position2D
import common._
import java.io.File
import skitch.helpers.FileLocation

trait SpriteLike
extends Position2D
//with AutoTransformer2D
with Render {
  def image:Image
  protected def imageOffset:vec2
  def scale:vec2
  def rotation: common.Radian
//  __transform = __transform & Transform {
//    gl.translate(-imageOffset)
//  }
  def render() {
    image.render()
  }
}

trait Spritely extends SpriteLike {

  def image:Image
  def imageCenter:vec2 = null
  var scale:vec2 = vec2.one

  lazy protected val imageOffset =
    if(imageCenter==null) vec(image.width/2, image.height/2) else imageCenter

}

class Sprite(
              val image:Image,
              var position:vec2,
              var scale:vec2 = vec2.one,
              var rotation:Radian = 0,
              imageCenter:vec2 = null
              )
extends SpriteLike {

  def this(loc:FileLocation, position:vec2) = this(Image.load(loc), position)

  lazy protected val imageOffset =
    if(imageCenter==null) vec(image.width/2, image.height/2) else imageCenter

}
