package skitch.audio

import java.nio.FloatBuffer
import org.lwjgl.{LWJGLException, BufferUtils}
import org.newdawn.slick.openal.{OggDecoder}
import org.lwjgl.openal.{AL10, AL}
import org.lwjgl.openal.AL10._
import skitch.vector.vec2

object SoundSystem {
	var listenerPos: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
	var listenerVel: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
	/**Orientation of the listener. (first 3 elements are "looking towards", second 3 are "up")
      Also note that these should be units of '1'. */
	var listenerOri: FloatBuffer = BufferUtils.createFloatBuffer(6).put(Array[Float](0.0f, 0.0f, -1.0f, 0.0f, 1.0f, 0.0f))
	listenerPos.flip
	listenerVel.flip
	listenerOri.flip
	private var initialized_? = false
	val oggDecoder = new OggDecoder

	def initialize() {
		if(! enabled) return
		try {
			AL.create(null, 44100, 15, true);
		}
		catch {
			case le: LWJGLException => {
				le.printStackTrace()
				return
			}
		}
		initialized_? = true
		AL10.alGetError
		AL10.alListenerf(AL_GAIN, 1f)
		AL10.alListener(AL10.AL_POSITION, listenerPos)
		AL10.alListener(AL10.AL_VELOCITY, listenerVel)
		AL10.alListener(AL10.AL_ORIENTATION, listenerOri)
		enabled = true
	}

	def destroy() = {
		AL.destroy()
	}

	def setListenerPos(pos:vec2) {
		listenerPos.clear()
		listenerPos.put(Array(pos.x, pos.y, 0))
		listenerPos.flip()
		AL10.alListener(AL10.AL_POSITION, listenerPos)
	}
	private var enabled = true
	def disable() {
		enabled = false
	}
}
