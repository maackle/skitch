package skitch.stage.box2d

import skitch.vector.vec2
import org.jbox2d.collision.shapes
import org.jbox2d.dynamics.{World, Filter, BodyType}


class EdgeBody(a:vec2, b:vec2)(implicit world:World) extends Embodied {
	val position = (a+b)/2

	val body = {
		val fixture = Embodied.defaults.fixtureDef
		val bodydef = Embodied.defaults.bodyDef

		val edge = new shapes.EdgeShape()
		edge.set(a,b)

		bodydef.`type` = BodyType.STATIC

		val filter = new Filter
		filter.categoryBits = 0x04
		filter.maskBits = 0xff - 1

		fixture.restitution = 0
		fixture.shape = edge
		fixture.userData = this
		fixture.filter = filter

		val body = world.createBody(bodydef)
		body.createFixture(fixture)
		body
	}
}
