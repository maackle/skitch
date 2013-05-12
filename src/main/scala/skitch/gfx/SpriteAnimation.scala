package skitch.gfx

import skitch.common._
import skitch.core.{Update, Render}
import skitch.vector.{vec, vec2}
import skitch.gfx.SpriteAnimation.FrameOptions


trait Animation extends Render with Update {
  object Mode extends Enumeration {
    val Once, Loop, PingPong = Value
  }
  type Mode = Mode.Value
  def pause()
  def play()
}


object SpriteAnimation {

  case class FrameOptions(durationMs:Int, offset:vec2=vec2.zero)

  val a = (FrameOptions.apply _).tupled
}

trait ImageAnimation extends Animation {
  def frames:Map[Image, FrameOptions]
  def images:IndexedSeq[Image]
}

class SpriteAnimation(var position:vec2, f:(Image, FrameOptions)*) extends SpriteLike with ImageAnimation {

  var scale = vec2.one
  var rotation = 0.0

  def this(position:vec2, images:Seq[Image], durationMs:Int, offset:vec2=null) = this(position, {
    val opt = FrameOptions(durationMs, offset)
    images.map((_, opt))
  } : _*)

  lazy val images = f.map(_._1).toIndexedSeq
  lazy val frames = f.toMap

  private val numFrames = images.size

  private var mode = Mode.Loop
  private var currentIndex = 0
//  private var currentOptions:FrameOptions = _
  private var direction = 1
  private var advance = true
  private var lastAdvance = milliseconds

  def image = {
    images(currentIndex)
  }

  //TODO: optimize
  def frame = frames(images(currentIndex))
  def imageOffset = if(frame.offset==null) vec(image.width/2, image.height/2) else frame.offset

  def play() { advance = true }
  def pause() { advance = false }

  def update(dt:Float) {
    if(advance && milliseconds - lastAdvance > frame.durationMs) {
      mode match {
        case Mode.Once => if(currentIndex < numFrames - 1) currentIndex += 1
        case Mode.Loop =>
          currentIndex = (currentIndex + direction) % numFrames
        case Mode.PingPong =>
          currentIndex = (currentIndex + direction) % numFrames
          direction = -direction
      }
      lastAdvance = milliseconds
    }
  }
}
