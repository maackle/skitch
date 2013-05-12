package skitch.core

import skitch.helpers.MemDouble
import org.lwjgl.opengl.Display
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11._
import skitch.helpers.MemDouble
import skitch.vector.{vec2, vec}
import skitch.common
import grizzled.slf4j.Logger
import org.newdawn.slick.opengl.InternalTextureLoader

object SkitchApp {
	val SlickTextureLoader = InternalTextureLoader.get()
}

trait SkitchApp extends App {

	val startState:StateBase
	val windowTitle:String
	val initialWindowSize:Option[(Int,Int)]

	protected[skitch] lazy val stateMachine = new StateMachine(startState)
	def currentState = stateMachine.current
	def changeState(state:StateBase) { stateMachine.change(state) }
	def pushState(state:StateBase) { stateMachine.push(state) }
	def popState() { stateMachine.pop() }

	InternalTextureLoader.get().setHoldTextureData(false)

	val fps = 60
	implicit val _app = this
	private var _tick = 0
	private val _loopTime, _executionTime = MemDouble(32)
	private val _startupTime = 0.0
	private var __timeToExit = false
	private[skitch] var _resourceLoaders = Set[ResourceLoader]()

	def exit() { __timeToExit = true }

	def windowSize = {
		assert(Display.isCreated)
		(Display.getWidth, Display.getHeight)
	}

	def windowRect = {
		val (w, h) = windowSize
		Rect(0, 0, w, h)
	}

	lazy val defaultEventSources = Seq(
		new KeyEventSource,
		new MouseEventSource
	)

	protected[skitch] def setProjection() {

		val vec2(x0, y0) = windowRect.bottomLeft
		val vec2(x1, y1) = windowRect.topRight
		glMatrixMode(GL_PROJECTION)
		glLoadIdentity()
		//    glOrtho(0, w, 0, h, -1, 1)
		glOrtho(x0, x1, y0, y1, -1, 1)
	}


	def avgExecutionTime = _executionTime.avg
	def avgFPS = 1000 / _loopTime.avg
	def lastFPS = 1000 / _loopTime.now
	def ticks = _tick
	def millis:Int = (common.milliseconds - _startupTime).toInt

	def initialize()
	def cleanup()

	def loopBody() {
		val dt = 1 / fps
		_resourceLoaders.foreach(_.update())
		currentState.__update(dt)
		currentState.__render()
		currentState.eventSources.foreach(_.update(dt))
		currentState.__handleEvents()
	}

	def run() {
		import common.milliseconds

		skitch.GLDisplay.create(windowTitle, initialWindowSize)
		skitch.GLView.default2D()

		initialize()

		var _ms = milliseconds
		while( ! Display.isCloseRequested() && ! __timeToExit ) {
			val loopStartTime = milliseconds
			loopBody()
			_tick += 1
			_loopTime << (milliseconds - _ms)
			_executionTime << (milliseconds - loopStartTime)
			_ms = loopStartTime
			Display.update()
			Display.sync(fps)
		}

		_resourceLoaders.foreach { rl =>
			if(rl.watchThread.isAlive) {
				rl.watchThread.interrupt()
				Logger("SkitchApp").debug("ended watch service: %s" format rl)
			}
		}


		cleanup()
	}

}
