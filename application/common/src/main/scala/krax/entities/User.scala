package krax.entities

import krax.domain.User._
import spray.json._

object UserEntities extends DefaultJsonProtocol {
	implicit val registrationError 	= jsonFormat3(RegistrationError)
	implicit val registeredUser 	= jsonFormat1(RegisteredUser)
}