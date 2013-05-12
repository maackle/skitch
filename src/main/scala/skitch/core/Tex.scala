package skitch.core


import org.newdawn.slick.opengl.{TextureLoader, TextureImpl, Texture}
import org.lwjgl.opengl.GL11._
import java.io.{FileInputStream, File}
import org.lwjgl.opengl.GL13
import skitch.gl
import skitch.helpers.FileLocation


class Tex(protected[skitch] val slickTexture:TextureImpl) {
  private val t = slickTexture
  val (getImageWidth, getImageHeight) = (t.getImageWidth, t.getImageHeight)
  val (getTextureWidth, getTextureHeight) = (t.getTextureWidth, t.getTextureHeight)
  val id = t.getTextureID
  def bind() {
    if(Tex.lastBound != t) {
      glBindTexture(GL_TEXTURE_2D, t.getTextureID)
      Tex.lastBound = t
    }
  }
}

//TODO: don't rely on singleton?
object Tex {
  private var _lastBound:TextureImpl = null
  def bindNone() { _lastBound = null }
  def lastBound = TextureImpl.getLastBind
  def lastBound_=(t:TextureImpl) { TextureImpl.bindByForce(t) }

//  def load(path:String) = {
//    val reg = """.*\.(.+?)$""".r
//    val reg(ext) = path
//    val texture = ext match {
//      case "png" | "gif" | "jpg" | "jpeg" => { TextureLoader.getTexture(ext, getStream(path)) }
//      case _ => throw new Exception("image format '%s' is not recognized".format(ext))
//    }
//    new Tex(texture)
//  }

  //TODO: test!
  def load(loc:FileLocation) = {
    val file = loc.file
    require(file.isFile, "bad file: %s" format loc)
    val reg = """.*\.(.+?)$""".r
    val path = file.getPath
    val reg(ext) = path
    val texture = ext match {
      case "png" | "gif" | "jpg" | "jpeg" => { TextureLoader.getTexture(ext, loc.stream).asInstanceOf[TextureImpl] }
      case _ => throw new Exception("image format '%s' is not recognized".format(ext))
    }
    new Tex(texture)
  }

  //  private var lastBoundId = -1
}


trait Textured {
  def tex:Tex

  def bindAnd(andThenDo: =>Unit) {
    //    glEnable(GL_TEXTURE_2D)
    GL13.glActiveTexture(GL13.GL_TEXTURE0)
    gl.texture2d {
      tex.bind()
      andThenDo
    }
  }
}

