package skitch


import org.lwjgl.input.{Keyboard => Kbd}

/* taken straight from Scage */

trait LWJGLKeyboard {

  type KeyEventId = Int
  lazy val MOUSE_LEFT_BUTTON   = 0
  lazy val MOUSE_RIGHT_BUTTON  = 1
  lazy val MOUSE_MIDDLE_BUTTON = 2  // need a test to prove that

  lazy val keyRange = 0 to 0xD3

  lazy val KEY_ESCAPE = Kbd.KEY_ESCAPE
  lazy val KEY_1 = Kbd.KEY_1
  lazy val KEY_2 = Kbd.KEY_2
  lazy val KEY_3 = Kbd.KEY_3
  lazy val KEY_4 = Kbd.KEY_4
  lazy val KEY_5 = Kbd.KEY_5
  lazy val KEY_6 = Kbd.KEY_6
  lazy val KEY_7 = Kbd.KEY_7
  lazy val KEY_8 = Kbd.KEY_8
  lazy val KEY_9 = Kbd.KEY_9
  lazy val KEY_0 = Kbd.KEY_0
  lazy val KEY_MINUS = Kbd.KEY_MINUS
  lazy val KEY_EQUALS = Kbd.KEY_EQUALS
  lazy val KEY_BACK = Kbd.KEY_BACK
  lazy val KEY_TAB = Kbd.KEY_TAB
  lazy val KEY_Q = Kbd.KEY_Q
  lazy val KEY_W = Kbd.KEY_W
  lazy val KEY_E = Kbd.KEY_E
  lazy val KEY_R = Kbd.KEY_R
  lazy val KEY_T = Kbd.KEY_T
  lazy val KEY_Y = Kbd.KEY_Y
  lazy val KEY_U = Kbd.KEY_U
  lazy val KEY_I = Kbd.KEY_I
  lazy val KEY_O = Kbd.KEY_O
  lazy val KEY_P = Kbd.KEY_P
  lazy val KEY_LBRACKET = Kbd.KEY_LBRACKET
  lazy val KEY_RBRACKET = Kbd.KEY_RBRACKET
  lazy val KEY_RETURN = Kbd.KEY_RETURN
  lazy val KEY_LCONTROL = Kbd.KEY_LCONTROL
  lazy val KEY_A = Kbd.KEY_A
  lazy val KEY_S = Kbd.KEY_S
  lazy val KEY_D = Kbd.KEY_D
  lazy val KEY_F = Kbd.KEY_F
  lazy val KEY_G = Kbd.KEY_G
  lazy val KEY_H = Kbd.KEY_H
  lazy val KEY_J = Kbd.KEY_J
  lazy val KEY_K = Kbd.KEY_K
  lazy val KEY_L = Kbd.KEY_L
  lazy val KEY_SEMICOLON = Kbd.KEY_SEMICOLON
  lazy val KEY_APOSTROPHE = Kbd.KEY_APOSTROPHE
  lazy val KEY_GRAVE = Kbd.KEY_GRAVE
  lazy val KEY_LSHIFT = Kbd.KEY_LSHIFT
  lazy val KEY_BACKSLASH = Kbd.KEY_BACKSLASH
  lazy val KEY_Z = Kbd.KEY_Z
  lazy val KEY_X = Kbd.KEY_X
  lazy val KEY_C = Kbd.KEY_C
  lazy val KEY_V = Kbd.KEY_V
  lazy val KEY_B = Kbd.KEY_B
  lazy val KEY_N = Kbd.KEY_N
  lazy val KEY_M = Kbd.KEY_M
  lazy val KEY_COMMA = Kbd.KEY_COMMA
  lazy val KEY_PERIOD = Kbd.KEY_PERIOD
  lazy val KEY_SLASH = Kbd.KEY_SLASH
  lazy val KEY_RSHIFT = Kbd.KEY_RSHIFT
  lazy val KEY_MULTIPLY = Kbd.KEY_MULTIPLY
  lazy val KEY_LMENU = Kbd.KEY_LMENU
  lazy val KEY_SPACE = Kbd.KEY_SPACE
  lazy val KEY_CAPITAL = Kbd.KEY_CAPITAL
  lazy val KEY_F1 = Kbd.KEY_F1
  lazy val KEY_F2 = Kbd.KEY_F2
  lazy val KEY_F3 = Kbd.KEY_F3
  lazy val KEY_F4 = Kbd.KEY_F4
  lazy val KEY_F5 = Kbd.KEY_F5
  lazy val KEY_F6 = Kbd.KEY_F6
  lazy val KEY_F7 = Kbd.KEY_F7
  lazy val KEY_F8 = Kbd.KEY_F8
  lazy val KEY_F9 = Kbd.KEY_F9
  lazy val KEY_F10 = Kbd.KEY_F10
  lazy val KEY_NUMLOCK = Kbd.KEY_NUMLOCK
  lazy val KEY_SCROLL = Kbd.KEY_SCROLL
  lazy val KEY_NUMPAD7 = Kbd.KEY_NUMPAD7
  lazy val KEY_NUMPAD8 = Kbd.KEY_NUMPAD8
  lazy val KEY_NUMPAD9 = Kbd.KEY_NUMPAD9
  lazy val KEY_SUBTRACT = Kbd.KEY_SUBTRACT
  lazy val KEY_NUMPAD4 = Kbd.KEY_NUMPAD4
  lazy val KEY_NUMPAD5 = Kbd.KEY_NUMPAD5
  lazy val KEY_NUMPAD6 = Kbd.KEY_NUMPAD6
  lazy val KEY_ADD = Kbd.KEY_ADD
  lazy val KEY_NUMPAD1 = Kbd.KEY_NUMPAD1
  lazy val KEY_NUMPAD2 = Kbd.KEY_NUMPAD2
  lazy val KEY_NUMPAD3 = Kbd.KEY_NUMPAD3
  lazy val KEY_NUMPAD0 = Kbd.KEY_NUMPAD0
  lazy val KEY_DECIMAL = Kbd.KEY_DECIMAL
  lazy val KEY_F11 = Kbd.KEY_F11
  lazy val KEY_F12 = Kbd.KEY_F12
  lazy val KEY_F13 = Kbd.KEY_F13
  lazy val KEY_F14 = Kbd.KEY_F14
  lazy val KEY_F15 = Kbd.KEY_F15
  lazy val KEY_KANA = Kbd.KEY_KANA
  lazy val KEY_CONVERT = Kbd.KEY_CONVERT
  lazy val KEY_NOCONVERT = Kbd.KEY_NOCONVERT
  lazy val KEY_YEN = Kbd.KEY_YEN
  lazy val KEY_NUMPADEQUALS = Kbd.KEY_NUMPADEQUALS
  lazy val KEY_CIRCUMFLEX = Kbd.KEY_CIRCUMFLEX
  lazy val KEY_AT = Kbd.KEY_AT
  lazy val KEY_COLON = Kbd.KEY_COLON
  lazy val KEY_UNDERLINE = Kbd.KEY_UNDERLINE
  lazy val KEY_KANJI = Kbd.KEY_KANJI
  lazy val KEY_STOP = Kbd.KEY_STOP
  lazy val KEY_AX = Kbd.KEY_AX
  lazy val KEY_UNLABELED = Kbd.KEY_UNLABELED
  lazy val KEY_NUMPADENTER = Kbd.KEY_NUMPADENTER
  lazy val KEY_RCONTROL = Kbd.KEY_RCONTROL
  lazy val KEY_NUMPADCOMMA = Kbd.KEY_NUMPADCOMMA
  lazy val KEY_DIVIDE = Kbd.KEY_DIVIDE
  lazy val KEY_SYSRQ = Kbd.KEY_SYSRQ
  lazy val KEY_RMENU = Kbd.KEY_RMENU
  lazy val KEY_PAUSE = Kbd.KEY_PAUSE
  lazy val KEY_HOME = Kbd.KEY_HOME
  lazy val KEY_UP = Kbd.KEY_UP
  lazy val KEY_PRIOR = Kbd.KEY_PRIOR
  lazy val KEY_LEFT = Kbd.KEY_LEFT
  lazy val KEY_RIGHT = Kbd.KEY_RIGHT
  lazy val KEY_END = Kbd.KEY_END
  lazy val KEY_DOWN = Kbd.KEY_DOWN
  lazy val KEY_NEXT = Kbd.KEY_NEXT
  lazy val KEY_INSERT = Kbd.KEY_INSERT
  lazy val KEY_DELETE = Kbd.KEY_DELETE
  lazy val KEY_LMETA = Kbd.KEY_LMETA
  lazy val KEY_RMETA = Kbd.KEY_RMETA
  lazy val KEY_APPS = Kbd.KEY_APPS
  lazy val KEY_POWER = Kbd.KEY_POWER
  lazy val KEY_SLEEP = Kbd.KEY_SLEEP
}

object LWJGLKeyboard extends LWJGLKeyboard
object Keys extends LWJGLKeyboard
