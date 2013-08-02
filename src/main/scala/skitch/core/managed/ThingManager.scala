package skitch.core.managed

import skitch.core.{Render, Update}

trait ThingManager extends Update {

	override def __update(dt:Float) {
		super.__update(dt)
		things.foreach(_.__update(dt))
	}

	protected def things:Iterable[Thing]
}
