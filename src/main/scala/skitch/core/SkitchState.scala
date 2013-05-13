package skitch.core

import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import skitch.vector.{vec3, vec2, vec}
import skitch.gl


trait StateBase extends Update with Render with EnterExit with EventSinkRoot {

	implicit val app:SkitchApp

	protected def onEnter:Unit
	protected def onExit:Unit
	protected[skitch] def beginLoop()
	protected[skitch] def endLoop()
}

abstract class SkitchState(val app:SkitchApp) extends StateBase {

	protected implicit val _app = app

	protected[skitch] def beginLoop() {

	}

	protected[skitch] def endLoop() {

	}
}

class StateMachine(startState:StateBase) {
	assert(startState != null)
	private val stack = collection.mutable.Stack[StateBase]()

	push(startState)

	def current = {
		assert(!stack.isEmpty)
		stack.top
	}

	def change(s:StateBase) {
		assert(!stack.isEmpty)
		current.__onExit
		stack.pop()
		stack.push(s)
		current.__onEnter
	}

	def push(s:StateBase) {
		stack.push(s)
		current.__onEnter
	}

	def pop() {
		assert(!stack.tail.isEmpty)
		val top = stack.pop()
		top.__onExit
	}
}
