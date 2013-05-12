package skitch.core

import skitch.gl

trait Hook {}

trait Update extends Hook {
  def update(dt:Float)
  private[skitch] def __update(dt:Float) = update(dt)
}

trait Render extends Hook {
  def render()
  private[skitch] def __render() = {
    render()
  }
}

trait EnterExit extends Hook {

  protected def onEnter()
  private[skitch] def __onEnter() = {
    onEnter
  }

  protected def onExit()
  private[skitch] def __onExit() = {
    onExit
  }
}
