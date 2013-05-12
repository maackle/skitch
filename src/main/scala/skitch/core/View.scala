package skitch.core

import skitch.gl
import skitch.vector.{vec, vec2, vec3}
import java.nio.{FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.GLU
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl
import skitch.common.implicits

trait Camera {

}


class View2D(windowBounds: =>Rect)(implicit app: SkitchApp) extends View { self =>

  lazy val bounds = windowBounds
  def this()(implicit app: SkitchApp) = this(app.windowRect)(app)

  class Camera2D extends Camera {
    var position: vec2 = vec2.zero//self.bounds.center
    var zoom: Float = 1f

    def centerOn(worldPoint:vec2) {
      position = worldPoint
    }
  }
  val camera = new Camera2D()

  def apply(drawing: =>Unit) {

    import implicits.float2int

    app.setProjection()

    glMatrixMode(GL_MODELVIEW)
    gl.matrix {
      gl.translate(self.bounds.center)
      gl.scale(camera.zoom, camera.zoom)
      gl.translate(-camera.position)

      if(bounds != app.windowRect) {
        val vec2(x, y) = bounds.bottomLeft
        val (w, h) = bounds.dimensions
        glScissor(x, y, w, h)
        glEnable(GL_SCISSOR_TEST)
//        gl.translate(bounds.center - app.windowRect.center)
      }

      transforms.update()
      drawing

      if(bounds != app.windowRect) {
        glDisable(GL_SCISSOR_TEST)
      }
    }

  }


  def toWorld(screen: vec2): vec2 = {
    import transforms._
    GLU.gluUnProject(screen.x, screen.y, 0, modelview, projection, viewport, position)
    vec(position.get(0), position.get(1))
  }

  def toScreen(world: vec2): vec2 = {
    import transforms._
    GLU.gluProject(world.x, world.y, 0, modelview, projection, viewport, position)
    vec(position.get(0), position.get(1))
  }
}

class View3D(val translation:vec3) extends View {

  def initializeCamera = {
    val vec3(eyeX, eyeY, eyeZ) = translation
    GLU.gluLookAt(
      eyeX,eyeY,eyeZ,
      0,0,0,
      0,1,0
    )
  }

  def apply(drawing: =>Unit) {
    gl.matrix {
      gl.translate(translation)
      transforms.update()
      drawing
    }
  }

  def toWorld(screen: vec2, winZ:Float): vec3 = {
    //    transforms.update() // TODO: Remove
    import transforms._
    GLU.gluUnProject(screen.x, screen.y, winZ, modelview, projection, viewport, position)
    vec(position.get(0), position.get(1), position.get(2))

  }

  def toScreen(world: vec3): vec2 = {
    //    transforms.update() // TODO: Remove
    import transforms._
    GLU.gluProject(world.x, world.y, world.z, modelview, projection, viewport, position)
    vec(position.get(0), position.get(1))
  }
}



trait View {

  def apply(drawing: =>Unit)

  object transforms {
    val viewport:IntBuffer = BufferUtils.createIntBuffer(16)
    val modelview:FloatBuffer = BufferUtils.createFloatBuffer(16)
    val projection:FloatBuffer = BufferUtils.createFloatBuffer(16)
    //    private val winZ:FloatBuffer = BufferUtils.createFloatBuffer(1)

    val position:FloatBuffer = BufferUtils.createFloatBuffer(3)

//    protected[kitsch]
    def update() {
      GL11.glGetFloat(GL11.GL_MODELVIEW_MATRIX, modelview)
      GL11.glGetFloat(GL11.GL_PROJECTION_MATRIX, projection)
      GL11.glGetInteger(GL11.GL_VIEWPORT, viewport)

      modelview.rewind()
      projection.rewind()
      viewport.rewind()

    }

  }
}
