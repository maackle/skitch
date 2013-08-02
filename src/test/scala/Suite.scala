
import grizzled.slf4j.Logging
import org.scalatest.{BeforeAndAfter, FunSuite}
import scala.collection.mutable.Stack
import skitch.core.managed.View2D
import skitch.core.{Camera2D, SkitchState, SkitchApp}
import skitch.vector.{vec2, vec}


class Suite extends FunSuite with Logging with BeforeAndAfter {

	class TestApp(
	             inUpdate:SkitchApp=>Unit = app=>()
	             ) extends SkitchApp { app =>

		class TestState extends SkitchState(app) {
			def onEnter = ()
			def onExit = ()
			def update(dt:Float) {
				inUpdate(app)
			}
			def render() {}
		}

		def cleanup() {}
		def initialize() {}

		val initialWindowSize = Some(800, 800)
		val fps = 60
		lazy val startState = new TestState
		val worldScale = 10f
		val windowTitle = "Test Game"
	}

	test("pop is invoked on an empty stack") {

		val emptyStack = new Stack[Int]
		intercept[NoSuchElementException] {
			emptyStack.pop()
		}
		assert(emptyStack.isEmpty)
	}

	test("toWorld and toScreen") {

		val app = new TestApp

		app.setup
		app.loopBody()
		app.currentState.render()

		val camera = new Camera2D
		val view = View2D(camera)(Nil)(app)

		view.camera.zoom = 2f
		view.camera.position = vec(1f, -1f)
		view.render() // updates the matrices

		val p = vec(10, 10)
		val world = view.toWorld(p)
		val screen = view.toScreen(p)

		assert(app.worldScale != 1, "test is meaningless with projectionScale==1")
		assert(world != vec2.zero && screen != vec2.zero, "view matrices are zero / have not been updated")
		assert(view.toScreen(world) != world, "toScreen does nothing")
		assert((view.toScreen(world) - view.toWorld(screen)).length < 0.0001f)

	}
}
