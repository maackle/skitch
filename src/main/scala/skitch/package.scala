
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.{DisplayMode, ContextAttribs, PixelFormat, Display}
import org.lwjgl.util.glu.GLU


package object skitch {

  object GLDisplay {
    def create(windowTitle:String, windowSize:Option[(Int,Int)], vsyncEnabled:Boolean = true) = {

      Display.setTitle(windowTitle)
      windowSize match {
        case Some((x,y)) => Display.setDisplayMode(new DisplayMode(x,y))
        case None =>
          Display.setFullscreen(true)
          Display.setVSyncEnabled(vsyncEnabled)
      }
      val pxfmt = new PixelFormat().withDepthBits(24).withSRGB(true)
      val ctxAttr = new ContextAttribs(3, 0)//.withForwardCompatible(true);

      Display.create(pxfmt, ctxAttr)
    }

    def destroy() = Display.destroy()
  }


  object GLView {

    def default2D() {
      //disable
      glDisable(GL_DEPTH_TEST)
      glDisable(GL_LIGHTING)

      // enable
      glEnable (GL_BLEND)
      glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }

    def default3D() {
      // disable
      glDisable(GL_LIGHTING)

      // enable
//      glEnable(GL_DEPTH_TEST)
      glEnable (GL_BLEND)
      glBlendFunc (GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA)
    }

    def perspective() {
      val width = Display.getWidth
      val height = Display.getHeight

      glViewport(0, 0, width, height)
      glMatrixMode(GL_PROJECTION)
      glLoadIdentity()
      GLU.gluPerspective(45f, width.toFloat / height.toFloat, 1f, 1000f)
      glMatrixMode(GL_MODELVIEW)
      glLoadIdentity()
    }

    def viewHUD() {
      import org.lwjgl.opengl.Display
      val width = Display.getDisplayMode.getWidth
      val height = Display.getDisplayMode.getHeight

      glMatrixMode(GL_PROJECTION)
      glLoadIdentity()
      glOrtho(0, width, 0, height, -1, 1)
      glMatrixMode(GL_MODELVIEW)
    }

  }
}
