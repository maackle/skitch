package skitch.core

import skitch.gl
import skitch.vector.{vec, vec2, vec3}
import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl
import skitch.common.implicits

trait Camera {

  def update(dt:Float) {}
}

class Camera2D extends Camera {
	var position: vec2 = vec2.zero//self.bounds.center
	var zoom: Float = 1f

	def centerOn(worldPoint:vec2) {
		position = worldPoint
	}

}

object View2D extends SkitchBase {
	def apply(CAMERA:Camera2D)(implicit APP:SkitchApp) = new View2D {
		val app = APP
		val camera = CAMERA
		def windowRect = app.windowRect
	}
	def apply(CAMERA:Camera2D, BOUNDS:Rect)(implicit APP:SkitchApp) = new View2D {
		val app = APP
		val camera = CAMERA
		val windowRect = BOUNDS
	}
}


trait View2D extends View { self =>

//	def state:SkitchState
	implicit def app:SkitchApp// = state.app
	def windowRect:Rect
	def viewportRect:Rect = Rect(toWorld(windowRect.bottomLeft), toWorld(windowRect.topRight))

	val camera:Camera2D

	def apply(drawing: =>Unit) {

		import implicits.float2int

		setProjection()

		glMatrixMode(GL_MODELVIEW)
		gl.matrix {
			gl.translate(windowRect.center * app.worldScale)
			gl.scale(camera.zoom, camera.zoom)
			gl.translate(-camera.position)

			if(windowRect != app.windowRect) {
				val vec2(x, y) = windowRect.bottomLeft
				val (w, h) = windowRect.dimensions
				glScissor(x, y, w, h)
				glEnable(GL_SCISSOR_TEST)
				//        gl.translate(bounds.center - app.windowRect.center)
			}

			transforms.update()
			drawing

			if(windowRect != app.windowRect) {
				glDisable(GL_SCISSOR_TEST)
			}
		}
	}

	protected[skitch] def setProjection() {
		app.setProjection()
	}

	def toWorld(screen: vec2): vec2 = {
		import transforms._
		GLU.gluUnProject(screen.x, screen.y, 0, modelview, projection, viewport, position)
		vec(position.get(0), position.get(1))
	}

	def toScreen(world: vec2): vec2 = {
		import transforms._
		GLU.gluProject(world.x, world.y, 0, modelview, projection, viewport, position)
		vec(position.get(0), position.get(1))
	}
}


trait View {

	def apply(drawing: =>Unit)

  def camera:Camera

	object transforms {
		val viewport:IntBuffer = BufferUtils.createIntBuffer(16)
		val modelview:FloatBuffer = BufferUtils.createFloatBuffer(16)
		val projection:FloatBuffer = BufferUtils.createFloatBuffer(16)
		//    private val winZ:FloatBuffer = BufferUtils.createFloatBuffer(1)

		val position:FloatBuffer = BufferUtils.createFloatBuffer(3)

		//    protected[kitsch]
		def update() {
			GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview)
			GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection)
			GL11.glGetInteger(GL11.GL_VIEWPORT, viewport)

			modelview.rewind()
			projection.rewind()
			viewport.rewind()

		}

	}
}
