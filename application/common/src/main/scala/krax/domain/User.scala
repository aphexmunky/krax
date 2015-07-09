package krax.domain

object User {
	case class GetEmail(username: String)
	case class GetEmailResponse(email: Option[String])

	case class RegistrationError(errorCode: Int, errorMessage: String, errorResolution: Option[String] = None)
	case class Register(username: String)
	case class RegisteredUser(username: String)

	val alreadyRegistered = RegistrationError(1, "Already registered", Some("Login or create a new account."))

	case class UpdateError(errorCode: Int, errorMessage: String, errorResolution: Option[String] = None)
	case class AddEmail(username: String, email: String)
	case class EmailAdded(email: String)

	val noUserToUpdate = UpdateError(2, "User doesn't exist to update against", Some("Updates must be done to a registered user."))
}
