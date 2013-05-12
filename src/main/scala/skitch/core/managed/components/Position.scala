package skitch.core.managed.components

import skitch.vector.{vec2, vec}
import skitch.Types._

import skitch.core.{components => plain, AutoTransform2D, AutoTransform, Render, managed}

trait Position extends Component with AutoTransform {
	def position: vec
}

trait Position2D extends Position with AutoTransform2D {
	def position: vec2
}

trait PositionXY extends Position2D {
	def x: Real
	def y: Real
	def x_=(x:Real)
	def y_=(y:Real)
	//  var x, y : Real
	def position = vec(x,y)
	def position_=(v:vec2) {
		x = v.x
		y = v.y
	}
}
