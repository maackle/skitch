package skitch.gfx


import skitch.{common, helpers, gl}
import skitch.vector.{vec2, vec}
import skitch.core.{Resource, ResourceLike, ImageResource, Render, ResourceDependent}
import skitch.helpers.FileLocation
import skitch.Types._


trait SpriteLike extends ResourceDependent with skitch.core.managed.components.Position2D {

	def image:ImageResource
	val resourceDependencies = Set(image).toSeq
	def render() { image.option.map(_.render()) }
}


class Sprite(val image:ImageResource, val position:vec2 = vec2.zero, val imageCenterInPixels: vec2 = null) extends SpriteLike {

	//TODO: better behavior on reload

	val scale = vec2.one
	var rotation = 0.0

	override def translation = position - image.is.origin

	def update(dt:Float) {}

	def refresh(changed:ResourceLike) {

	}
}
