package skitch.core.managed

import skitch.core.{Render, Update}

trait ThingManager {

	protected def things:Iterable[Thing]
}
