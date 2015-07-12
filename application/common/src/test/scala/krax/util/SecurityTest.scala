package krax.util

import collection.mutable.Stack
import org.scalatest._
import krax.util.Security._

class SecurityTest extends FlatSpec with Matchers {

  "A Security SHA256 instance" should "generate a random password" in {
    val security = new Security with SHA256

    val firstPassword = security.randomPassword
    val secondPassword = security.randomPassword

    firstPassword should not be (secondPassword)
  }

  it should "generate a sha256 hash with 64 characters" in {
    val security = new Security with SHA256

    val random = security.randomPassword
    val hash = security.secureHash(random)

    hash.hash should have length 64
  }

  it should "match a sha256 pattern" in {
    val security = new Security with SHA256

    val random = security.randomPassword
    val hash = security.secureHash(random)

    hash.hash should fullyMatch regex """[a-f0-9]{64}"""    
  }

  "A Security BCrypt instance" should "generate a random password" in {
    val security = new Security with BCrypt

    val firstPassword = security.randomPassword
    val secondPassword = security.randomPassword

    firstPassword should not be (secondPassword)
  }

  it should "generate a bcrypt hash with 120 characters" in {
    val security = new Security with BCrypt

    val random = security.randomPassword
    val hash = security.secureHash(random)

    hash.hash should have length 120
  }

  it should "match a bcrypt pattern" in {
    val security = new Security with BCrypt

    val random = security.randomPassword
    val hash = security.secureHash(random)

    hash.hash should fullyMatch regex """[0-9a-f]{120}"""    
  }

}