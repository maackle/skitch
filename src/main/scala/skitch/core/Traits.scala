package skitch.core

import skitch.gl
import grizzled.slf4j.Logging

trait SkitchBase extends Logging

trait Hook extends SkitchBase

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

private[skitch] trait ManagedRender extends Render {

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
