package skitch

package object helpers {

	def pairs[T](seq:Seq[T]) = {
		if (seq.size>1)
			seq.slice(0,seq.size-1).zip(seq.slice(1,seq.size))
		else
			throw new Exception("must have at least 2 elements to form pairs")
	}

	def pairsLoop[T](seq:Seq[T]) = {
		if (seq.size>1) {
			val b = seq.slice(1, seq.size) :+ seq.head
			seq.zip(b)
		}
		else
			throw new Exception("must have at least 2 elements to form pairs")
	}

	def pairwise[T](seq:Seq[T])(fn:(T,T)=>Unit) {
		seq.reduceLeft {
			(a:T, b:T) => {
				fn(a,b)
				b
			}
		}
	}


	object Radian {
		val pi = math.Pi
		val pi2 = math.Pi*2
		def clampS(in:Double)= {
			var a = in
			while (a  >  pi) a -= pi2
			while (a <= -pi) a += pi2
			a
		}
		def clampU(in:Double) = {
			var a = in
			while (a > pi2) a -= pi2
			while (a < 0)   a += pi2
			a
		}
		def diff(a:Double, b:Double) = {
			clampS(clampS(a) - clampS(b))
			//         (a,b) = (clampS(a), clampS(b))
			//         val d = b - a
			//         if(d > pi) d - pi2
			//         if(d < pi) d + pi2
		}
	}

}
