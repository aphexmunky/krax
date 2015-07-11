package krax.util

import collection.mutable.Stack
import org.scalatest._
import krax.util.Security._

class SecurityTest extends FlatSpec with Matchers {

  "A Security instance" should "generate a random password" in {
    val security = new Security with SHA256

    val firstPassword = security.randomPassword
    val secondPassword = security.randomPassword

    firstPassword should not be (secondPassword)
  }

}