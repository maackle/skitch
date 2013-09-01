package skitch.core

import skitch.vector.vec2


object Event {
  type Id = Int
  object Null extends Event { val code = -1 }
}

trait Event {
//  def code:Event.Id
}

trait KeyEvent extends Event {
//  def code:Int
}

case class KeyHold(code:Int) extends KeyEvent
case class KeyDown(code:Int) extends KeyEvent
case class KeyUp(code:Int) extends KeyEvent

trait MouseEvent extends Event {
  def windowPos:vec2
}

case class MouseHold(code:Int, windowPos:vec2) extends MouseEvent
case class MouseDown(code:Int, windowPos:vec2) extends MouseEvent
case class MouseUp(code:Int, windowPos:vec2) extends MouseEvent
