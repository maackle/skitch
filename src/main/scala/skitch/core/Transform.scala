package skitch.core

import skitch.gl
import skitch.vector.{vec, vec2}
import skitch.core.components.{Position, Position2D}
import skitch.Types._
import grizzled.slf4j.Logging

trait TransformBase extends SkitchBase { self =>

	def inject()

	@inline def apply(glBlock: =>Unit) {
		gl.matrix {
			inject()
			glBlock
		}
	}

	def +(other:TransformBase) = new TransformBase {
		def inject() {
			self.inject()
			other.inject()
		}
	}
}

class Transform(glBlock: ()=>Unit) extends TransformBase {

	def inject() {
		glBlock()
	}
}

object Transform {

	def apply(glBlock: =>Unit) = {
		new Transform(() => {glBlock})
	}
}


trait InternalTransform extends ManagedRender {
	protected def __transform:Transform

	private [skitch] override def __render() {
		if(__transform!=null) __transform.apply { super.__render() }
		else super.__render()
	}
}

private[skitch] trait AutoTransform extends InternalTransform with Position {
	def translation: vec
	def scale: vec
	def rotation: Radian

}

trait AutoTransform2D extends AutoTransform with Position2D {
	def translation: vec2 = position
	def scale: vec2
	def rotation: Radian

	protected val __transform = Transform {
		gl.translate(translation)
		gl.scale(scale)
		gl.rotateRad(rotation)
	}

}
