package skitch.gfx

import skitch.{common, helpers, gl}
import skitch.vector.{vec2, vec}
import skitch.core._
import skitch.core.components._
import skitch.helpers.FileLocation
import skitch.Types._

trait SpriteLike extends ResourceDependent with Position2D with AutoAffine2D {

	implicit val app:SkitchApp

	def image:ImageResource
	def dimensionsPx = { image.is.dimensions }
	def dimensions = { image.is.dimensions * app.worldScale }
	val resourceDependencies = Set(image).toSeq
	def render() = gl.matrix {
		gl.scale2(app.worldScale)
		image.option.map(_.render())
	}

	override def translation = position
}


trait Sprite extends SpriteLike {

	//TODO: better behavior on refresh
	//TODO: honestly, don't need this right now, can just merge this into SpriteLike probably...

	val image:ImageResource

	def onRefresh(changed:ResourceLike) {

	}
}
