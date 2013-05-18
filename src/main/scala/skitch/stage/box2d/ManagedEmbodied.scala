package skitch.stage.box2d

import org.jbox2d.dynamics._
import org.jbox2d.collision.shapes
import skitch.vector.vec2
import skitch.core.components.{Position2D, Velocity2D, CircleShape}
import skitch.Types
import skitch.core.managed.SkitchState
import org.jbox2d.common.Vec2
import skitch.core.{SkitchBase, AutoAffine2D}

trait Embodied extends SkitchBase with B2Implicits {
	val body:Body
}

trait ManagedEmbodied extends Embodied with Velocity2D with AutoAffine2D {

	val scaling = vec2.one

	def position:vec2 = body.getPosition
	def velocity:vec2 = body.getLinearVelocity
	def rotation = body.getAngle

	def update(dt:Float) {
		body.getType match {
			case BodyType.STATIC =>
			case BodyType.KINEMATIC =>
				warnKinematic
			case BodyType.DYNAMIC =>
//				position.set(body.getPosition)
//				velocity.set(body.getLinearVelocity)
//				rotation = body.getAngle.toDouble
		}
	}

	private lazy val warnKinematic = {
		warn("ManagedEmbodied update() for kinematic type not implemented")
	}
}

object Embodied {

	object defaults {
		val linearDamping = 0f
		val angularDamping = 0f
		val restitution = 1f
		val friction = 0f
		val density = 1f

		def bodyDef = {
			val b = new BodyDef
			b.`type` = BodyType.DYNAMIC
			b.linearDamping = linearDamping
			b.angularDamping = angularDamping
			b
		}
		def fixtureDef = {
			val f = new FixtureDef()
			f.density = density
			f.restitution = restitution
			f.friction = friction
			f
		}
	}
}
