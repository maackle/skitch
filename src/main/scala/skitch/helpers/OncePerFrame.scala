package skitch.helpers

import skitch.core.SkitchApp


class OncePerFrame[A](val calc:()=>A)(implicit app:SkitchApp) {
	private var lastFrame = -1
	private var cache:A = _

	def apply():A = {
		if (app.ticks > lastFrame) {
			cache = calc()
			lastFrame = app.ticks
		}
		cache
	}
}

object OncePerFrame {

	implicit def framecached2val[A](fc:OncePerFrame[A]) = fc()

	def apply[A](calc: =>A)(implicit app:SkitchApp) = new OncePerFrame( () => calc)(app)
}
