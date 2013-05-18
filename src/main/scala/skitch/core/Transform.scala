package skitch.core

import skitch.gl
import skitch.vector.{vec, vec2}
import skitch.core.components.{Position, Position2D}
import skitch.Types._
import grizzled.slf4j.Logging

trait GLOps extends SkitchBase { self =>

	def inject()

	@inline def apply(glBlock: =>Unit) {
		gl.matrix {
			inject()
			glBlock
		}
	}

	def +(other:GLOps) = new GLOps {
		def inject() {
			self.inject()
			other.inject()
		}
	}
}

object GLOps {

	def apply(glBlock: =>Unit) = {
		new GLOps {
			def inject() { glBlock }
		}
	}
}


trait InternalTransform extends Render {
	protected def __transform:GLOps

	private [skitch] override def __render() {
		if(__transform!=null) __transform.apply { super.__render() }
		else super.__render()
	}
}

private[skitch] trait AutoAffine extends InternalTransform with Position {
	def translation: vec
	def scaling: vec
	def rotation: Radian
}

trait AutoAffine2D extends AutoAffine with Position2D {
	def translation: vec2 = position
	def scaling: vec2
	def rotation: Radian

	protected val __transform = GLOps {
		gl.translate(translation)
		gl.scale(scaling)
		gl.rotateRad(rotation)
	}

}

