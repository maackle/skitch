package skitch.core

package scalene.core

import org.lwjgl.opengl.{GL11, GL13, GL15}
import org.lwjgl.opengl.GL11._
import org.lwjgl.opengl.GL15._
import org.lwjgl.BufferUtils
import java.nio.{FloatBuffer, IntBuffer, DoubleBuffer, Buffer}
import scala.Some
import skitch.{common, Color}
import common._
import skitch.Color
import skitch.vector.{vec2, vec3}

abstract class VboBuffer[+B <: Buffer](val length:Int) {
  val id = GL15.glGenBuffers()
}

class VboFloatBuffer(len:Int) extends VboBuffer[FloatBuffer](len) {

  val buffer:FloatBuffer = BufferUtils.createFloatBuffer(len)

  def set(vs:Array[Float], mode:Int) = {
    buffer.put(vs)
    buffer.flip()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, mode)
    this
  }

  def setRaw(vs:Array[Float], mode:Int) = set(vs, mode)
}

class VboVec2Buffer(len:Int) extends VboFloatBuffer(len*2) {
  def set(ps:Array[VBO.v], mode:Int) = {
    super.set(ps flatMap ( p => Seq(p.x, p.y)), mode)
  }
}

class VboColorBuffer(len:Int) extends VboFloatBuffer(len*4) {
  def set(ps:Array[Color], mode:Int) = {
    super.set(ps flatMap ( p => Seq(p.r, p.g, p.b, p.a)), mode)
  }
}

class VboIntBuffer(len:Int) extends VboBuffer[IntBuffer](len) {
  val buffer:IntBuffer = BufferUtils.createIntBuffer(len)

  def set(a:Array[Int], mode:Int = GL15.GL_STREAM_DRAW) = {
    buffer.put(a)
    buffer.flip()
    GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, id)
    GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer, mode)
    this
  }
}

trait VBO {
  def vertices:VboVec2Buffer
  def colors:Option[VboColorBuffer]
  def texCoords:Option[VboVec2Buffer]
  def indices:Option[VboIntBuffer]

  def dim:Int = 2

  import VBO._

  def setVertices(vs:Array[v]) = vertices.set(vs, GL15.GL_STATIC_DRAW )
  def setColors(vs:Array[Color]) = colors.get.set(vs, GL15.GL_STATIC_DRAW)
  def setTexCoords(vs:Array[v]) = texCoords.get.set(vs, GL15.GL_STATIC_DRAW)
  def setIndices(vs:Array[Int]) = indices.get.set(vs, GL15.GL_STATIC_DRAW)
  def updateVertices(vs:Array[v]) = vertices.set(vs, GL15.GL_STREAM_DRAW)
  def updateColors(vs:Array[Color]) = colors.get.set(vs, GL15.GL_STREAM_DRAW)
  def updateTexCoords(vs:Array[v]) = texCoords.get.set(vs, GL15.GL_STREAM_DRAW)
  def updateIndices(vs:Array[Int]) = indices.get.set(vs, GL15.GL_STREAM_DRAW)

  protected def wrapDraw(fn: =>Unit) {
    glEnableClientState(GL_VERTEX_ARRAY)
    if(texCoords.isDefined) glEnableClientState(GL_TEXTURE_COORD_ARRAY)
    if(colors.isDefined) glEnableClientState(GL_COLOR_ARRAY)

    glBindBuffer(GL_ARRAY_BUFFER, vertices.id)
    glVertexPointer(dim, GL_FLOAT, 0, 0)

    colors map { colors =>
      glBindBuffer(GL_ARRAY_BUFFER, colors.id)
      glColorPointer(4, GL_FLOAT, 0, 0)
    }

    texCoords map { texCoords =>
      glBindBuffer(GL_ARRAY_BUFFER, texCoords.id)
      GL13.glClientActiveTexture(GL13.GL_TEXTURE0)
      glTexCoordPointer(2, GL_FLOAT, 0, 0)
    }

    fn

    glBindBuffer(GL_ARRAY_BUFFER, GL_NONE)
    glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, GL_NONE)
    glDisableClientState(GL_VERTEX_ARRAY)
    glDisableClientState(GL_COLOR_ARRAY)
    glDisableClientState(GL_TEXTURE_COORD_ARRAY)
  }

  def draw(method:Int) = wrapDraw {
    indices match {
      case Some(indices) =>
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indices.id)
        glDrawElements(method, indices.length, GL_UNSIGNED_INT, 0)
      case _ =>
        glDrawArrays(method, 0, vertices.length)
    }
  }
}

object VBO {

  type v = vec2
  val dim = 2

  def create(size:Int, useTextures:Boolean=false, useIndices:Boolean=false, useColors:Boolean=false):VBO = {
    val n = size

    val vs = new VboVec2Buffer(n)

    val ts = if (useTextures) Some(new VboVec2Buffer(n)) else None

    val cs = if (useColors) Some(new VboColorBuffer(n)) else None

    val ixs = if (useIndices) Some(new VboIntBuffer(n)) else None

    new VBO {
      val vertices = vs
      val colors = cs
      val texCoords = ts
      val indices = ixs
    }
  }

  def createAndLoad(vertices:Array[v], colors:Array[Color]=null, texCoords:Array[v]=null, indices:Array[Int]=null):VBO = {
    val n = vertices.length

    val vbo = create(n,
      useTextures = texCoords != null,
      useColors   = colors != null,
      useIndices  = indices != null
    )

    val S = GL15.GL_STATIC_DRAW
    vbo.setVertices(vertices)

    if(texCoords!=null) {
      assert(n == texCoords.length)
      vbo.setTexCoords(texCoords)
    }
    if(colors!=null) {
      assert(n == colors.length)
      vbo.setColors(colors)
    }
    if(indices!=null) {
      vbo.setIndices(indices)
    }

    vbo

  }

}
