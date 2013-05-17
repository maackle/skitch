package skitch.stage.box2d

import org.jbox2d.dynamics._
import org.jbox2d.collision.shapes
import skitch.vector.vec2
import skitch.core.components.{Position2D, Velocity2D, CircleShape}
import skitch.Types
import skitch.core.managed.SkitchState
import org.jbox2d.common.Vec2
import skitch.core.AutoAffine2D

trait Embodied extends Velocity2D with AutoAffine2D with B2Implicits {

	val velocity = vec2.zero
	val scale = vec2.one
	var rotation = 0.0

	def world:World

	def update(dt:Float) {
		body.getType match {
			case BodyType.STATIC =>
			case BodyType.KINEMATIC =>
				warnKinematic
			case BodyType.DYNAMIC =>
				position.set(body.getPosition)
				velocity.set(body.getLinearVelocity)
				rotation = body.getAngle.toDouble
		}
	}

	lazy val warnKinematic = {
		warn("Embodied update() for kinematic type not implemented")
	}

	val body:Body
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
