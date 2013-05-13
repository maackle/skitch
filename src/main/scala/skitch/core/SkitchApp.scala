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

trait SkitchApp extends App with SkitchBase {

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

	def projectionRect = {
		val (w, h) = windowSize
		Rect(0, 0, w*projectionScale, h*projectionScale)
	}

	val projectionScale:Float

	protected[skitch] def setProjection() {
		val vec2(x0, y0) = projectionRect.bottomLeft
		val vec2(x1, y1) = projectionRect.topRight
		glMatrixMode(GL_PROJECTION)
		glLoadIdentity()
		glOrtho(x0, x1, y0, y1, -1, 1)
	}

	lazy val defaultEventSources = Seq(
		new KeyEventSource,
		new MouseEventSource
	)

	def avgExecutionTime = _executionTime.avg
	def avgFPS = 1000 / _loopTime.avg
	def currentFPS = 1000 / _loopTime.now
	def ticks = _tick
	def millis:Int = (common.milliseconds - _startupTime).toInt

	def initialize()
	def cleanup()

	def loopBody() {
		val dt:Float = 1.0f / fps.toFloat
		_resourceLoaders.foreach(_.update())
		currentState.beginLoop()
		currentState.__update(dt)
		currentState.__render()
		currentState.eventSources.foreach(_.update(dt))
		currentState.__handleEvents()
		currentState.endLoop()
	}

	def run() {
		import common.milliseconds

		skitch.GLDisplay.create(windowTitle, initialWindowSize)
		skitch.GLView.default2D()

		initialize()

		var _ms = milliseconds
		while( ! Display.isCloseRequested() && ! __timeToExit ) {
			assert(fps > 0)
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
