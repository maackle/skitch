package skitch.vector

object vec3 {
  def zero = vec3(0,0,0)
  def one = vec3(0,1,1)

  def apply(X:Double, Y:Double, Z:Double) = new vec3 {
    x = X.toFloat
    y = Y.toFloat
    z = Z.toFloat
  }

  def unapply(v:vec3) = {
    Some(v.x, v.y, v.z)
  }
}

trait vec3 extends vec {

//  def x:V
//  def y:V
//  def z:V
//  def x_=(X:V)
//  def y_=(Y:V)
//  def z_=(Z:V)

  var x,y,z:V = 0

  def isValid():Boolean = !x.isNaN && !y.isNaN && !z.isNaN

  @inline def lengthSquared:V = x*x + y*y + z*z
  @inline def length:V = math.sqrt(lengthSquared).toFloat

  def project(other:vec3):vec3 = {
    val denom = (other dot other)
    if(denom < eps) return vec3.zero
    else return other * ((this dot other)/denom)
  }
  def unit = {
    val len = length
    if(len < eps || len.isNaN) vec3.zero
    else this / len
  }
  def limit(cap:Float) = {
    val len = length
    if (len > cap && !len.isNaN) {
      vec3(
        x * cap/len,
        y * cap/len,
        z * cap/len
      )
    }
    else this
  }
  def manhattan = math.abs(x) + math.abs(y) + math.abs(z)

  def flipX = vec(-x, y, z)
  def flipY = vec(x, -y, z)
  def flipZ = vec(x, y, -z)

  def +(v:vec3):vec3 = vec3(x+v.x, y+v.y, z+v.z)
  def -(v:vec3):vec3 = vec3(x-v.x, y-v.y, z-v.z)
  def dot(v:vec3):Float = (x*v.x + y*v.y + z*v.z)
  def *(c:Float):vec3 = vec3(x*c, y*c, z*c)
  def *(c:Double):vec3 = vec3(x*c toFloat, y*c toFloat, z*c toFloat)
  def *(v:vec3):vec3 = vec3(x*v.x, y*v.y, z*v.z)
  def /(c:Float):vec3 = vec3(x/c, y/c, z/c)

  @inline
  def +=(v:vec3) { x += v.x; y += v.y; z += v.z }
  @inline
  def -=(v:vec3) { x -= v.x; y -= v.y; z -= v.z }
  @inline
  def *=(v:vec3) = { x *= v.x; y *= v.y; z *= v.z }
  @inline
  def *=(c:Float) = { x *= c; y *= c; z *= c }
  @inline
  def /=(c:Float) = { x /= c; y /= c; z /= c }

  def unary_- :vec3 = vec3(-x, -y, -z)

  def <(v:vec3) = x < v.x && y < v.y && z < v.z
  def <=(v:vec3) = x <= v.x && y <= v.y && z <= v.z
  def >(v:vec3) = x > v.x && y > v.y && z > v.z
  def >=(v:vec3) = x >= v.x && y >= v.y && z >= v.z

  override def toString = "vec3(%.3f, %.3f, %.3f)".format(x,y,z)
}
