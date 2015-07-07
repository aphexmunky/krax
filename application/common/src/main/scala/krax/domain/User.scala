package krax.domain

object User {
	case class GetEmail(username: String)
	case class GetEmailResponse(email: Option[String])

	case class Register(username: String, email: String)
	case class Registered(username: String, email: String)	
}
