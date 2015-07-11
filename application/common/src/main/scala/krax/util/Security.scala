package krax.util

import java.security.SecureRandom

import com.roundeights.hasher.Implicits._
import scala.language.postfixOps
import scala.util.Random

object Security {

	val defaultPasswordSecurity = new Security with SHA256

	sealed case class SecureHash(hash: String, salt: Array[Byte])

	trait Security { this: Algorithm =>
		def randomPassword: String = Random.alphanumeric.take(13).mkString
		def secureHash(str: String): SecureHash = {
			val random = new SecureRandom()
			val salt: Array[Byte] = Array.fill(8)(0)
			random.nextBytes(salt)
			val hash = alg.hash(str, salt)
			SecureHash(hash, salt)
		}
	}
	
	trait Algorithm {
		val alg: AlgorithmImplementation
		trait AlgorithmImplementation {
			def hash(str: String, salt: Array[Byte]): String
		}
	}

	trait SHA256 extends Algorithm {
		val alg = new SHA256Security
		class SHA256Security extends AlgorithmImplementation {
			def hash(str: String, salt: Array[Byte]): String = {
				str.salt(salt).sha256
			}
		}
	}

	trait BCrypt extends Algorithm {
		val alg = new BCryptSecurity
		class BCryptSecurity extends AlgorithmImplementation {
			def hash(str: String, salt: Array[Byte]): String = {
				str.salt(salt).bcrypt
			}
		}
	}
}