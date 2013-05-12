package skitch.core.managed.components

import skitch.core.{components => plain}
import skitch.core.managed.Thing

trait Component extends Thing with plain.Component
