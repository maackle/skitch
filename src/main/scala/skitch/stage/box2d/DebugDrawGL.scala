package skitch.stage.box2d

import skitch.vector.vec
import org.jbox2d.common.{Transform, Color3f, Vec2, OBBViewportTransform}
import org.jbox2d.callbacks.DebugDraw
import org.jbox2d.callbacks.DebugDraw._
import org.jbox2d.collision.AABB
import skitch.{gfx, gl}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL11
import grizzled.slf4j.Logging

object DebugDrawGL extends {
	val viewport = new OBBViewportTransform()
} with DebugDraw(viewport) with B2Implicits with Logging {

	def glColorB2(color:Color3f) {
		glColor3f(color.x, color.y, color.z)
	}

	setFlags(
//		e_jointBit|
//        e_pairBit|
//		e_centerOfMassBit
		//      e_aabbBit|
		e_shapeBit
	)
	viewport.setYFlip(true)
	viewport.setExtents(1f, 1f)
	def drawCircle(center:Vec2, radius:Float, color:Color3f) {
		glPushMatrix()
		glColorB2(color)
		gl.translate(center)
		glScalef(radius, radius, 1)
		gfx.fill(false)
		gfx.unitCircle(30)
		glPopMatrix()
	}

	def drawPoint(center:Vec2, radius:Float, color:Color3f) {
		glColorB2(color)
		drawCircle(center, radius/100f, color)
	}

	def drawSegment(a:Vec2, b:Vec2, color:Color3f) {
		glColorB2(color)
		gfx.line(a,b)
	}
	def notimpl() {
		throw new UnsupportedOperationException("DebugDraw function not implemented")
	}
	def drawAABB(argAABB:AABB, color:Color3f) = notimpl()
	def drawSolidCircle(center:Vec2, radius:Float, axis:Vec2, color:Color3f) {
		glColorB2(color)
		drawCircle(center, radius, color)
	}
	def drawSolidPolygon(vertices:Array[Vec2], count:Int, color:Color3f) {
		glColorB2(color)
		gl.begin(GL_POLYGON) {
			for (v <- vertices.slice(0, count)) glVertex2f(v.x, v.y)
		}
	}
	def drawString(x:Float, y:Float, s:String, color:Color3f) {notimpl()}
	def drawTransform(xf:Transform) {
		Vector(xf.p, vec.polar(10, xf.q.getAngle))
	}
}
