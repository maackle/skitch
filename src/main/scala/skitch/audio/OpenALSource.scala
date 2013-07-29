package skitch.audio

import java.nio.{ByteBuffer, FloatBuffer, IntBuffer}
import org.lwjgl.BufferUtils
import org.lwjgl.openal.AL10
import org.lwjgl.openal.AL10._
import java.io.InputStream
import org.newdawn.slick.openal.OggDecoder
import org.lwjgl.util.WaveData
import skitch.vector.vec2
import org.newdawn.slick
import skitch.core.{OggResource, Resource}
import skitch.helpers.FileLocation


trait SoundBase {
	def playing:Boolean
	def play(forceRestart:Boolean=false):SoundBase
	def pause():SoundBase
	def stop():SoundBase
	def gain(level:Float):SoundBase
	def loop(on:Boolean):SoundBase
}

case class AudioData(data: ByteBuffer, format:Int, samplerate:Int)

object OpenALSource {
	def loadOgg(is:InputStream) = {
		val dec = new OggDecoder()
		val ogg = dec.getData(is)
		val fmt = if(ogg.channels > 1) AL10.AL_FORMAT_STEREO16 else AL10.AL_FORMAT_MONO16
		AudioData(ogg.data, fmt, ogg.rate)
	}

	def loadWave(is:InputStream) = {
		val wav = WaveData.create(is)
		AudioData(wav.data, wav.format, wav.samplerate)
	}
}


class Sound(ogg:OggResource) extends SoundBase {

//	object source {
//		def is = (new OpenALSource).load(ogg.is)
//	}

	val source = ogg.map( d => (new OpenALSource).load(d))

	def playing = source.is.playing
	def play(forceRestart:Boolean=true) = { source.is.play(forceRestart); this }
	def pause() = { source.is.pause(); this }
	def stop() = { source.is.stop(); this }
	def gain(level:Float) = { source.is.gain(level); this }
	def loop(on:Boolean) = { source.is.loop(on); this }

}

class OpenALSource() {

	private val buffer: IntBuffer = BufferUtils.createIntBuffer(1)
	private val sourcebuf: IntBuffer = BufferUtils.createIntBuffer(1)
	private val sourcePos: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))
	private val sourceVel: FloatBuffer = BufferUtils.createFloatBuffer(3).put(Array[Float](0.0f, 0.0f, 0.0f))

	def setPos(pos:vec2):OpenALSource = {
		sourcePos.rewind()
		sourcePos.put(Array(pos.x, pos.y, 0))
		sourcePos.flip()
		AL10.alSource(id, AL10.AL_POSITION, sourcePos)
		this
	}

	def setVel(vel:vec2) = {
		sourceVel.rewind()
		sourceVel.put(Array(vel.x, vel.y, 0))
		sourceVel.flip()
		AL10.alSource(id, AL10.AL_VELOCITY, sourceVel)
		this
	}

	def setAttenuation(ref:Float, max:Float, rolloff:Float) = {
		setf(AL_REFERENCE_DISTANCE, ref)
		setf(AL_MAX_DISTANCE, max)
		setf(AL_ROLLOFF_FACTOR, rolloff)
		this
	}

	def playing = alGetSourcei(id, AL_SOURCE_STATE) == AL_PLAYING

	def check(complaint:String="reality check"):Boolean = {
		import AL10._
		val err = AL10.alGetError
		val reason = err match {
			case AL_NO_ERROR => return true
			case AL_INVALID_NAME => "Invalid name parameter."
			case AL_INVALID_ENUM => "Invalid parameter."
			case AL_INVALID_VALUE => "Invalid enum parameter value."
			case AL_INVALID_OPERATION => "Illegal call."
			case AL_OUT_OF_MEMORY => "Unable to allocate memory."
			case _ => "Unknown error"
		}
		throw new Exception("%s - al error: %s (%d)".format(complaint, reason, err))
	}

	def id:Int = sourcebuf.get(0)

	private[skitch] def load(audioData:AudioData):OpenALSource = {
		val AudioData(dat, fmt, rate) = audioData
		AL10.alGenBuffers(buffer)
		check("11")
		AL10.alBufferData(buffer.get(0), fmt, dat, rate)
		check("22")
		AL10.alGenSources(sourcebuf)
		check("33")
		AL10.alSourcei(id, AL10.AL_BUFFER, buffer.get(0))
		check("44")
		AL10.alSourcef(id, AL10.AL_PITCH, 1.0f)
		AL10.alSourcef(id, AL10.AL_GAIN, 1.0f)
		check("55")

		setPos(vec2.zero)
		setVel(vec2.zero)
		this
	}

	private def setf(attr:Int, v:Float) = AL10.alSourcef(sourcebuf.get(0), attr, v)
	private def seti(attr:Int, v:Int) = AL10.alSourcei(sourcebuf.get(0), attr, v)

	def gain(v:Float):OpenALSource = { setf(AL10.AL_GAIN, v); this}
	def volume(level:Float) = { gain(math.sqrt(level*level).toFloat) }
	def loop(v:Boolean):OpenALSource = { seti(AL10.AL_LOOPING, if(v) 1 else 0); this }
	def play(forceRestart:Boolean=true) = {
		if(forceRestart || ! playing) {
			AL10.alSourcePlay(sourcebuf.get(0))
		}
		this
	}
	def pause() {
		AL10.alSourcePause(sourcebuf.get(0))
	}
	def stop() {
		AL10.alSourceStop(sourcebuf.get(0))
	}
	def destroy() {
		stop()
		AL10.alDeleteSources(sourcebuf)
		AL10.alDeleteBuffers(buffer)
	}
}
