package skitch.gfx

import javax.imageio.ImageIO

/**
 * Created with IntelliJ IDEA.
 * User: michael
 * Date: 1/13/13
 * Time: 5:34 PM
 * To change this template use File | Settings | File Templates.
 */

trait SpriteSheetLike {

  val columns, rows:Int
  val width, height: Int
  protected val baseImage:Image

//  protected val images = {
//    if(columns >= 1 && rows >= 1) Array.ofDim[Image](columns, rows)
////    else if(xDim > 1 && yDim == 1) Array.ofDim[Image](xDim)
////    else if(yDim > 1 && xDim == 1) Array.ofDim[Image](yDim)
//    else if(columns == 1 && rows == 1) Array.ofDim[Image](1, 1)
//    else throw new Exception("Can't initialize SpriteSheetLike with xDim or yDim < 1")
//  }

  def images:Array[Image]

  /**
   * get the image at the zero-indexed indices specified
   *
   * @param column
   * @param row
   * @return
   */
  def at(column:Int, row:Int):Image = {
    images( column + row*columns )
  }

}

class SpriteSheet(protected val baseImage:Image, val columns:Int, val rows:Int) extends SpriteSheetLike {

  lazy val (width, height) = (baseImage.width, baseImage.height)
  lazy val images:Array[Image] = for {
    row <- Array.range(0, rows)
    col <- Array.range(0, columns)
    x = col * width / columns
    y = row * height / rows
  } yield {
    baseImage.copy(cliprect = Some(ClipRect(x,y, width/columns, height/rows)))
  }
}
