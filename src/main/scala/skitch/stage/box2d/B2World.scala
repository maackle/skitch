package skitch.stage.box2d

import skitch.core.managed.SkitchState
import org.jbox2d.dynamics.World
import skitch.Types


trait B2World extends SkitchState {

	implicit val world:World
	val b2scale:Types.Real

	def velocityIterations :Int
	def positionIterations :Int

	lazy val initialize = {
		world.setDebugDraw(DebugDrawGL)
	}

	override def update(dt: Float) {
		initialize
		world.step(dt, velocityIterations, positionIterations)
		super.update(dt)
	}
}
