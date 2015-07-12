package krax.domain

import krax.rest.Request._

object User {
    case class GetEmail(username: String)
    case class GetEmailResponse(email: Option[String])

    case class RegistrationError(errorCode: Int, errorMessage: String, errorResolution: Option[String] = None)
    case class Register(username: String, requestDetails: RequestDetails)
    case class RegisteredUser(username: String)

    val alreadyRegistered = RegistrationError(1, "Already registered", Some("Login or create a new account."))

    case class UpdateError(errorCode: Int, errorMessage: String, errorResolution: Option[String] = None)
    case class AddEmail(username: String, email: String, requestDetails: RequestDetails)
    case class EmailAdded(email: String)

    val noUserToUpdate = UpdateError(2, "User doesn't exist to update against", Some("Updates must be done to a registered user."))

    case object CreateNewPassword
    case class PasswordSet(pw: String, salt: Array[Byte])
}
