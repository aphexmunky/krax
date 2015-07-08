package krax.rest.routing.user

import krax.rest.routing.BackendCall
import krax.domain.User._

import spray.routing.HttpService
import spray.http.StatusCodes._
import akka.pattern.ask
import scala.util.{ Success, Failure }

import scalaz.\/

trait UserService extends HttpService {
  this: BackendCall =>

  implicit def executionContext = actorRefFactory.dispatcher

  lazy val usersRoute = pathPrefix("users") {
    createUser
  }

  def createUser = pathPrefix("register") {
    post {
      formFields('username) { username =>
        val f = (backend ? Register(username)).mapTo[\/[RegistrationError,RegisteredUser]]
        onComplete(f) {
          case Success(v) if v.isRight  => complete(OK, "test" + v)
          case Success(v) if v.isLeft   => complete(OK, "test" + v)
          case Failure(_)               => complete(NotFound)
        }
      }
    }
  }

}