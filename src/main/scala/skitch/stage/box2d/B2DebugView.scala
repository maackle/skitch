package skitch.stage.box2d

import skitch.core.{SkitchApp}
import skitch.core.managed
import org.jbox2d.dynamics.World

class B2DebugView(implicit val app:SkitchApp, world:World) extends managed.View2D {
	lazy val windowBounds = app.windowRect

	val things = Nil

	override def render() {
		apply {
			world.drawDebugData()
		}
	}

}
