package skitch.vector

import scala.math._
import grizzled.slf4j.Logging

trait vec {
  type V = vec.V
  type Radian = vec.Radian
  val eps = vec.EPS
}

object vec extends Logging { self =>

  type V = Float
  type Radian = Double
  val EPS:V = 1e-4f

  private object Random {
    val rand = new scala.util.Random()
    def uniform(lo: Double = 0.0, hi: Double = 1.0): Double = (hi - lo) * rand.nextDouble + lo
    def uniform(lo: Float, hi: Float): Float = (hi - lo) * rand.nextFloat + lo
    def gaussian(mean: Double, std: Double): Double = rand.nextGaussian * std + mean
  }

  def apply(a: (Float, Float)) = vec2(a._1, a._2)
//  def apply(a: (Double, Double)) = vec2(a._1, a._2)
  def apply(x: Float, y: Float) = vec2(x,y)
  def apply(x: Double, y: Double) = vec2(x,y)

  def apply(a: (Float, Float, Float)) = vec3(a._1, a._2, a._3)
//  def apply(a: (Double, Double, Double)) = vec3(a._1, a._2, a._3)
  def apply(x: Float, y: Float, z:Float) = vec3(x,y,z)
  def apply(x: Double, y: Double, z:Double) = vec3(x,y,z)


  /*** 2-D ***/

  def lerp(a:vec2, b:vec2, t:V) = {
    if(0 <= t && t <= 1) {}
    else warn("lerp() warning: t=%f out of range[0,1]" format t)
    a*(1-t) + b*t
  }

  def pow(v:vec2, p:Double) = {
    vec(math.pow(v.x.toDouble, p), math.pow(v.y.toDouble, p))
  }

  object polar {
    def apply(r:Float, t:Radian) = vec2(r*cos(t), r*sin(t))
    def apply(r:Double, t:Radian) = vec2(r*cos(t), r*sin(t))
    def apply(p:(V, Radian)) = vec2(p._1*cos(p._2), p._1*sin(p._2))
    def apply(v:vec2):vec2 = apply(v.length, v.angle)
    def random(r:Float, exp:Float=0.5f) = polar(math.pow(Random.uniform(0,1), exp) * r, Random.uniform(0,math.Pi*2))
    def randomSector(r:Float, ang0:Radian, ang1:Radian, exp:Float=0.5f) = polar(math.pow(Random.uniform(0,1), exp) * r, Random.uniform(ang0, ang1))
  }

  /*** 3-D ***/

  def lerp(a:vec3, b:vec3, t:V) = {
    if(0 <= t && t <= 1.000001) {}
    else printf("lerp() warning: t=%f out of range[0,1]", t)
    a*(1-t) + b*t
  }
//  def pow(v:vec3, p:Double) = {
//    vec3(math.pow(v.x, p), math.pow(v.y,p), math.pow(v.z,p))
//  }
}
