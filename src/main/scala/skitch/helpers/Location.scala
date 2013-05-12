package skitch.helpers

import java.io.{FileInputStream, File}
import grizzled.slf4j.Logging

trait Location {

}

class FileLocation(_file:File) extends Location with Logging {
  lazy val file = new File(fullPath)
  def stream = {
    val s = new FileInputStream(file)
    info("file: " + file.length())
    info("available: " + s.available())
    s
  }
  val fullPath = _file.getAbsolutePath
}
