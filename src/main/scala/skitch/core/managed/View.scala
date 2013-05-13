package skitch.core.managed

import skitch.gl
import skitch.vector.{vec, vec2, vec3}
import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl
import skitch.common.implicits
import skitch.{core => plain}
import skitch.core.{Render, Camera, Rect, SkitchApp}

trait View extends plain.View with Render with ThingManager {

}

//TODO: make a class so user can override render()
object View2D {
	def apply(THINGS: Iterable[Thing])(implicit APP:SkitchApp) = new View2D {
		val app = APP
		lazy val windowBounds = app.windowRect.copy()
		def things = THINGS
	}
	def apply(BOUNDS:Rect)(THINGS: Iterable[Thing])(implicit APP:SkitchApp) = new View2D {
		val app = APP
		val windowBounds = BOUNDS
		def things = THINGS
	}
}

trait View2D extends plain.View2D with View {

	def render() {
		apply {
			things.foreach(_.__render())
		}
	}
}


