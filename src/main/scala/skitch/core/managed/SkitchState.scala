package skitch.core.managed

import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import skitch.vector.{vec3, vec2, vec}
import skitch.{core => plain, Color, gl}

import skitch.core.SkitchApp

trait StateBase extends plain.StateBase {

	implicit val app:SkitchApp

	protected[skitch] def onEnter:Unit
	protected[skitch] def onExit:Unit

}

abstract class SkitchState(app:SkitchApp) extends plain.SkitchState(app) with ThingManager {

	private var _tick = 0

	def ticks = _tick

	protected def views:Seq[View]

	def backgroundColor:Option[Color]

	def update(dt: Float) {
		things.foreach(_.update(dt))
		_tick += 1
	}

	def render() {
		backgroundColor.map { color =>
			gl.clear(color)
		}
		views.foreach(_.render())
	}

}
