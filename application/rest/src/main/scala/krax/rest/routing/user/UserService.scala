package krax.rest.routing.user

import krax.rest.routing.BackendCall
import krax.domain.User._

import spray.routing.HttpService
import spray.http.StatusCodes._
import akka.pattern.ask
import scala.util.{ Success, Failure }

trait UserService extends HttpService {
  this: BackendCall =>

  implicit def executionContext = actorRefFactory.dispatcher

  lazy val myRoute = defaultRoute

  def defaultRoute = path(Rest) { username =>
    get {
    	val f = (backend ? GetEmail(username)).mapTo[GetEmailResponse]
      onComplete(f) {
        case Success(emailResponse)   => complete(emailResponse.toString)
        case Failure(_)               => complete(NotFound)
      }
    } ~
    post {
      formFields('email) { email =>
        complete {
          backend ! Register(username, email)
          Accepted
        }
      }
    }
  }
}