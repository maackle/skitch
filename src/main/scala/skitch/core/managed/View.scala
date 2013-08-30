package skitch.core.managed

import skitch.{core => plain}
import skitch.core._

trait View extends plain.View with Render with ThingManager {
	def update(dt:Float) {
    camera.update(dt)
  }
}

//TODO: make a class so user can override render()
object View2D {

	def apply(CAMERA:Camera2D)(THINGS: Iterable[Thing])(implicit APP:SkitchApp):View2D = new View2D {
		val app = APP
		val camera = CAMERA
		lazy val windowRect = app.windowRect.copy()
		def things = THINGS
	}

	def apply(BOUNDS:Rect, CAMERA:Camera2D)(THINGS: Iterable[Thing])(implicit APP:SkitchApp):View2D = new View2D {
		val app = APP
		val windowRect = BOUNDS
		val camera = CAMERA
		def things = THINGS
	}
}

trait View2D extends plain.View2D with View {

	def render() {
		apply {
			things.foreach(_.__render())
		}
	}
}


