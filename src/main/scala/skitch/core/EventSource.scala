package skitch.core

import scala.collection.mutable
import skitch.helpers.MemBoolean
import org.lwjgl.input.{Mouse}
import skitch.vector.vec

import org.lwjgl.input.{Keyboard => Kbd}

trait EventSource[+Unused <: Event] extends Update { self =>
  protected def raise(ev:Event) {
    eventQueue += ev
  }

  protected val eventQueue = mutable.ListBuffer[Event]()

  def presentTo(sink:EventSinkBase) {
    for {
      ev <- eventQueue
      op <- sink.__handler.consumeImmediately(ev)
    } {
      //      debug("event consumed: %s %s" format (ev, op) )
    }
  }
}

/* TODO: make eventDomain dynamic to really limit the key checks,
 * maybe passing in a changeable list of all EventSinks,
 * and when it changes, go through and check isDefinedAt for each value
 * */
object KeyEventSource extends EventSource[KeyEvent] {
  private val keyState = collection.mutable.Map[Event.Id, MemBoolean]()
  private val downKeys = collection.mutable.Set[Event.Id]()

  def update(dt:Float) {

    eventQueue.clear()

    while(Kbd.next) {
      val code = Kbd.getEventKey
      val down = Kbd.getEventKeyState
      if(down) {
        raise( KeyDown(code) )
        downKeys += code
      }
      else {
        raise( KeyUp(code) )
        downKeys -= code
      }
    }

    for(code <- downKeys) raise( KeyHold(code) )
  }
}


object MouseEventSource extends EventSource[MouseEvent] {

  private val buttonState = collection.mutable.Map[Event.Id, MemBoolean]()

  def update(dt:Float) = {

    eventQueue.clear()

    while(Mouse.next) {
      val code = Mouse.getEventButton
      val down = Mouse.getEventButtonState
      val pos = vec(Mouse.getEventX, Mouse.getEventY)
      if(code >= 0) {
        val state = buttonState.getOrElseUpdate(code, MemBoolean(2))
        state << down
        if (down)
          raise(MouseHold(code, pos))
        if (state.xOn)
          raise(MouseDown(code, pos))
        else if (state.xOff)
          raise(MouseUp(code, pos))
      }
    }
  }

}
