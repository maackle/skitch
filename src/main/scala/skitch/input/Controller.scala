package skitch.input

import net.java.games.input.{Component, Controller, ControllerEnvironment}
import Component.Identifier
import Component.Identifier.Button
import grizzled.slf4j.Logging
import skitch.vector.{vec, vec2}
import skitch.core.{Event, EventSource, SkitchBase, Update}
import skitch.helpers.{MemBoolean}

object ControllerSystem extends Logging {

  lazy val ctlEnv:ControllerEnvironment = ControllerEnvironment.getDefaultEnvironment

  def initialize() {
    ctlEnv
  }

  protected def allControllers = ctlEnv.getControllers().toIndexedSeq
  protected def allGamepads = allControllers.filter(_.getType == Controller.Type.GAMEPAD)
  def X360Controllers = allGamepads.filter(_.getName.toLowerCase.matches(""".*xbox\s*360.*""")).map(X360(_))
}

abstract class InputDevice extends Update with SkitchBase {

}

abstract class Gamepad extends InputDevice {
  val ctl:Controller
  override def toString = ctl.toString
}

trait X360Event extends Event

trait X360ButtonEvent extends X360Event {
  val id:Button
}

trait X360POVEvent extends X360Event

trait X360Analog2DEvent extends X360Event {
  def position:vec2
}

trait X360StickEvent extends X360Analog2DEvent
trait X360RightStickEvent extends X360Analog2DEvent

object X360 {
  import Component.Identifier.{Button=>btn}
  val A = btn.A
  val B = btn.B
  val X = btn.X
  val Y = btn.Y
  val L = btn.LEFT
  val R = btn.RIGHT
  val stickLeft = btn.LEFT_THUMB3
  val stickRight = btn.RIGHT_THUMB3
  val START = btn.UNKNOWN
  val SELECT = btn.SELECT
  val XBOX = btn.MODE

  type POVType = Float

  case class ButtonHold(id:Button) extends X360ButtonEvent
  case class ButtonDown(id:Button) extends X360ButtonEvent
  case class ButtonUp(id:Button) extends X360ButtonEvent

  case class POVHold(id:POVType) extends X360POVEvent
  case object POVUp extends X360POVEvent

  case class Stick(position:vec2) extends X360StickEvent
  case object StickOff extends X360StickEvent { val position = vec2.zero }

  private def makeMap() = Seq(A, B, X, Y, L, R, stickLeft, stickRight, START, SELECT, XBOX
//    UP, DOWN, LEFT, RIGHT
  ).map { b =>
    (b.getName, new MemBoolean(2))
  }
  .toMap
}


case class X360(ctl:Controller) extends Gamepad with EventSource[X360Event] {

  logger.info("found %d rumbler(s)".format(ctl.getRumblers.size))

  import X360._

  val deadzone = 0.05f
  val buttons = ctl.getComponents.map(c => (c.getIdentifier, new MemBoolean(2, false))).toMap
  val memStick = new MemBoolean(2, false)
  val stick = vec2.zero
  val stickRight = vec2.zero
  var triggerLeft:Float = 0f
  var triggerRight:Float = 0f

  def update(dt:Float) {
    eventQueue.clear()

    import Identifier.{Axis, Button}
    ctl.poll()

    for(c <- ctl.getComponents) {
      val z = c.getDeadZone
      var d = c.getPollData

      if(math.abs(d) < math.max(z, deadzone)) d = 0
      c.getIdentifier match {
        case Axis.X =>
          stick.x = d
        case Axis.Y =>
          stick.y = -d
        case Axis.RX =>
          stickRight.x = d
        case Axis.RY =>
          stickRight.y = -d
        case Axis.Z =>
          triggerLeft = d
        case Axis.RZ =>
          triggerRight = d
        case Axis.POV =>
          if (d == Component.POV.OFF) raise(X360.POVUp)
          else raise(X360.POVHold(d))
        case b:Button =>
          val ix = b
          if (buttons.contains(ix)) {
            buttons(ix) << (d == 1)
            if (buttons(ix).xOn) raise(ButtonDown(b))
            else if (buttons(ix).xOff) raise(ButtonUp(b))
            else if (buttons(ix).now) raise(ButtonHold(b))
          } else {
//            logger.error("key not found: " + b)
          }
        case _ =>
      }
    }

    memStick << stick.nonZero
    if (memStick.xOff)
      raise(StickOff)
    else
      raise(Stick(stick))
  }

}
