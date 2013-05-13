package skitch.gfx

import skitch.gl
import skitch.vector.{vec2, vec, vec3}
import scala.math._
import org.lwjgl.opengl.GL11._

trait GFX extends gl.GL {

  def line(a:vec3, b:vec3) = {

  }

  type R = Float
  type Vertex = (vec.V, vec.V)
  type VertexList = Array[Vertex]

  private var circleCache:Map[ Int, VertexList ] = Map()

  @inline
  def bindVertices(verts:Array[vec2]) {
    for(v <- verts) vertex(v)
  }

  @inline
  def bindVertices(verts:VertexList) {
    for(v <- verts) {
      vertex(v._1, v._2)
    }
  }
  @inline
  protected def getCircle(num:Int):VertexList = {
    if (!circleCache.isDefinedAt(num)) {
      circleCache += num -> {
        for (i:Int <- Array.range(0, num)) yield ( cos(2.0*Pi*i/num).toFloat , sin(2.0*Pi*i/num).toFloat )
      }
    }
    circleCache(num)
  }

  @inline def unitCircle(num:Int=100) {
    if (!circleCache.isDefinedAt(num)) {
      circleCache += num -> {
        for (i:Int <- Array.range(0, num)) yield ( cos(2.0*Pi*i/num).toFloat , sin(2.0*Pi*i/num).toFloat )
      }
    }
    begin(GL_POLYGON) {
      bindVertices(circleCache(num))
    }
  }


  @inline def circle(radius:R, center:vec2=null, num:Int=0) {
    @inline def guessCircleNum(radius:R) = 16

    glPushMatrix()
    if(center!=null) translate(center)
    scale(radius, radius)
    unitCircle(if(num>0) num else guessCircleNum(radius))
    glPopMatrix()
  }

  def points(points: VertexList) {
    glBegin(GL_POINTS)
    for (v <- points) vertex(v._1, v._2)
    glEnd()
  }

  def line(v1:vec2, v2:vec2) {
    glBegin(GL_LINES)
    vertex(v1)
    vertex(v2)
    glEnd()
  }

  def vector(origin:vec2, to:vec2) {
    line(origin, origin + to)
  }

  def rect(w:R, h:R) {
    val pos = -vec(w/2, h/2)
    gl.begin(GL_POLYGON) {
      skitch.gl.vertex(pos)
      skitch.gl.vertex(pos + vec(0, h))
      skitch.gl.vertex(pos + vec(w, h))
      skitch.gl.vertex(pos + vec(w, 0))
    }
  }

  def rect(center:vec2, w:R, h:R) {
    val pos = center - vec(w/2, h/2)
    gl.begin(GL_POLYGON) {
      gl.vertex(pos)
      gl.vertex(pos + vec(0, h))
      gl.vertex(pos + vec(w, h))
      gl.vertex(pos + vec(w, 0))
    }
  }

}
