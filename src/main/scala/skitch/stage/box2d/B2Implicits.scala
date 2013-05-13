package skitch.stage.box2d

import org.jbox2d.common.Vec2
import skitch.vector.vec2

trait B2Implicits {

	implicit def b2vec2_vec2(v:Vec2) = vec2(v.x, v.y)
	implicit def vec2_b2vec2(v:vec2) = new Vec2(v.x, v.y)

}
