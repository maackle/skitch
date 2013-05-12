package skitch.core

import skitch.common.Op
import skitch.LWJGLKeyboard


object EventHandler {
  type Partial = PartialFunction[Event, Any]
  type Lifted = Function[Event, Option[Any]]

  def apply(fn:Partial) = new EventHandler(fn)
}

class EventHandler(val lifted:EventHandler.Lifted) {

  def this(partial:EventHandler.Partial) = this(partial.lift)

  def consumeImmediately[A <: Event](ev:A) = {
    lifted(ev)
  }

  def ++(other:EventHandler) = new EventHandler( ev => {
    lifted(ev).orElse(other.lifted(ev))
  })

}

trait EventSink {

  val app:SkitchApp

  protected[skitch] var __handler:EventHandler = EventHandler {
    case KeyDown(LWJGLKeyboard.KEY_ESCAPE) => app.exit()
  }

//  implicit def fn2handler(fn:EventHandler.Partial) = EventHandler(fn)
  lazy val eventSources = app.defaultEventSources

  def listen(fn:EventHandler.Partial) {
    __handler = __handler ++ EventHandler(fn)
  }

  def listenTo(handler:EventHandler) {
    __handler = __handler ++ handler

  }

  protected[skitch] def __handleEvents() {
    eventSources.foreach(_.presentTo(this))
  }
}
