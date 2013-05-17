package skitch.core

import skitch.gl
import skitch.vector.{vec, vec2}
import skitch.core.components.{Position, Position2D}
import skitch.Types._
import grizzled.slf4j.Logging

trait OpenGLOps extends SkitchBase { self =>

	def inject()

	@inline def apply(glBlock: =>Unit) {
		gl.matrix {
			inject()
			glBlock
		}
	}

	def +(other:OpenGLOps) = new OpenGLOps {
		def inject() {
			self.inject()
			other.inject()
		}
	}
}

object OpenGLOps {

	def apply(glBlock: =>Unit) = {
		new OpenGLOps {
			def inject() { glBlock }
		}
	}
}

trait Transform extends OpenGLOps {

}


trait InternalTransform extends ManagedRender {
	protected def __transform:OpenGLOps

	private [skitch] override def __render() {
		if(__transform!=null) __transform.apply { super.__render() }
		else super.__render()
	}
}

private[skitch] trait AutoAffine extends InternalTransform with Position {
	def translation: vec
	def scale: vec
	def rotation: Radian
}

trait AutoAffine2D extends AutoAffine with Position2D {
	def translation: vec2 = position
	def scale: vec2
	def rotation: Radian

	protected val __transform = OpenGLOps {
		gl.translate(translation)
		gl.scale(scale)
		gl.rotateRad(rotation)
	}

}

