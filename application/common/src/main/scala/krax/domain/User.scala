package krax.domain

object User {
	case class GetEmail(username: String)
	case class GetEmailResponse(email: Option[String])

	case class RegistrationError(errorCode: Int, errorMessage: String, errorResolution: Option[String] = None)
	case class Register(username: String)
	case class RegisteredUser(username: String)	

	val alreadyRegistered = RegistrationError(1, "Already registered", Some("Login or create a new account"))
}
