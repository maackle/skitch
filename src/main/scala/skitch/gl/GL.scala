package skitch.gl

import org.lwjgl.opengl.GL11._
import skitch.Color
import skitch.vector.{vec, vec3, vec2}

trait GL {

	def matrix(fn: => Any) {
		glPushMatrix()
		fn
		glPopMatrix()
	}

	def begin(what: Int)(fn: => Any) {
		def cancel() {
			glEnd()
		}
		glBegin(what)
		fn
		glEnd()
	}

	def enable(what: Int)(fn: => Any) {
		def cancel() {
			glDisable(what)
		}
		glEnable(what)
		fn
		glDisable(what)
	}

	def texture2d = enable(GL_TEXTURE_2D) _

	@inline
	def fill(yes: Boolean) {
		if (yes) glPolygonMode(GL_FRONT_AND_BACK, GL_FILL)
		else glPolygonMode(GL_FRONT_AND_BACK, GL_LINE)
	}

	@inline
	def clear(color: Color) {
		glClearColor(color.r, color.g, color.b, 0)
		glClear(GL_COLOR_BUFFER_BIT)
	}

	@inline def lineWidth(w: Float) {
		glLineWidth(w)
	}

	@inline def translate(v: vec2) { translate(v.x, v.y) }
	@inline def translate(v: vec3) { glTranslatef(v.x, v.y, v.z) }
	@inline def translate(x: Float, y: Float) = glTranslatef(x, y, 0)
	@inline def translate(x: Double, y: Double) = glTranslated(x, y, 0)
	@inline def translate(x: Float, y: Float, z: Float) = glTranslatef(x, y, z)
	@inline def translate(x: Double, y: Double, z: Float) = glTranslated(x, y, z)
	def translate(v:vec) {
		v match {
			case v:vec2 => translate(v)
			case v:vec3 => translate(v)
		}
	}

	@inline def scale(sx: Float, sy: Float) { glScalef(sx, sy, 1) }
	@inline def scale(sx: Double, sy: Double) { glScaled(sx, sy, 1) }
	@inline def scale(sx: Float, sy: Float, sz: Float) { glScalef(sx, sy, sz) }
	@inline def scale(sx: Double, sy: Double, sz: Double) { glScaled(sx, sy, sz) }
	@inline def scale(s:vec2) { scale(s.x, s.y) }
	@inline def scale(s:vec3) { scale(s.x, s.y) }
	def scale(v:vec) {
		v match {
			case v:vec2 => scale(v)
			case v:vec3 => scale(v)
		}
	}

	@inline def rotateRad(t: Float) { glRotatef(t.toDegrees, 0, 0, 1) }
	@inline def rotateRad(t: Double) { glRotated(t.toDegrees, 0, 0, 1) }
	@inline def rotateDeg(t: Float) { glRotatef(t, 0, 0, 1) }
	@inline def rotateDeg(t: Double) { glRotated(t, 0, 0, 1) }

	@inline def vertex(v: vec2) { vertex(v.x, v.y) }
	@inline def vertex(x: Float, y: Float) = glVertex2f(x, y)
	@inline def vertex(x: Double, y: Double) = glVertex2d(x, y)

	@inline def vertex(v: vec3) { vertex(v.x, v.y, v.z) }
	@inline def vertex(x: Float, y: Float, z: Float) = glVertex3f(x, y, z)
	@inline def vertex(x: Double, y: Double, z: Double) = glVertex3d(x, y, z)

}
