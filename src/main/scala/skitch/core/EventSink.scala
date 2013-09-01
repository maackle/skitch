package skitch.core

import skitch.common.Op
import skitch.LWJGLKeyboard


object EventHandler {
	type Partial = PartialFunction[Event, Any]
	type Lifted = Function[Event, Option[Any]]
	lazy val Null = new EventHandler({ case _ => None })
}

class EventHandler(val partial:EventHandler.Partial) extends SkitchBase {

	import EventHandler._

	lazy val lifted = partial.lift

	def consumeImmediately[A <: Event](ev:A):Option[Any] = {
		lifted.apply(ev)
	}

	def ++(other:EventHandler) = new EventHandler(
		partial.orElse(other.partial)
	)

}

sealed trait EventSinkBase extends SkitchBase with LWJGLKeyboard {

	val app:SkitchApp

	protected[skitch] var __handler:EventHandler

	def eventSources = app.defaultEventSources

	private var sinks:Set[EventSink] = Set.empty

	def listenTo(sinks:EventSink*) {
		this.sinks ++= sinks
	}

	def unlistenTo(sinks:EventSink*) {
		this.sinks --= sinks
	}

	def listen(fn:EventHandler.Partial) {
		__handler = new EventHandler(fn) ++ __handler
	}

	def listenToHandler(handler:EventHandler) {
		__handler = handler ++ __handler
	}

	protected[skitch] def __handleEvents() {
		eventSources.foreach(_.presentTo(this))
		sinks.foreach(_.__handleEvents())
	}

	protected[skitch] def __collectEventSources():Set[EventSource[Event]] = {
		eventSources ++ sinks.flatMap(_.__collectEventSources())
	}
}

trait EventSink extends EventSinkBase {

	protected[skitch] var __handler:EventHandler = EventHandler.Null

}

trait EventSinkRoot extends EventSinkBase {

	protected[skitch] var __handler:EventHandler = new EventHandler( {
		case KeyDown(LWJGLKeyboard.KEY_ESCAPE) => app.exit()
		case _ =>
	})

}
