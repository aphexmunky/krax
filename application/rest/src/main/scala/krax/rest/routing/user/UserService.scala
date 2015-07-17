package krax.rest.routing.user

import krax.rest.routing.BackendService
import krax.domain.User._
import krax.rest.Request._
import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import scala.util.{ Success, Failure }
import scalaz._

class UserService extends SprayJsonSupport {
  import krax.entities.UserEntities._

  implicit val registeredUser     = jsonFormat1(RegisteredUser)

  lazy val usersRoute = pathPrefix("users") {
    createUser ~ updateUser
  }

  def createUser = pathPrefix("register") {
    post {
      formFields("username") { username =>
        val requestDetails = RequestDetails()
        val f = (backend ? Register(username, requestDetails)).mapTo[\/[RegistrationError, RegisteredUser]]
        onComplete(f) {
          case Success(\/-(res)) => complete(OK, res.toString)
          case Success(-\/(res)) => complete(Conflict, res.toString)
          case Failure(_)        => complete(NotFound)
          case msg               => complete(NotFound, s"There was an issue with the request: [$msg]")
        }
      }
    }
  }

  def updateUser = pathPrefix("update" / Segment) { username =>
    pathPrefix("email") {
      post {
        formFields("email") { email =>
          val requestDetails = RequestDetails()
          val f = (backend ? AddEmail(username, email, requestDetails)).mapTo[\/[UpdateError, EmailAdded]]
          onComplete(f) {
            case Success(\/-(res)) => complete(OK, res.toString)
            case Success(-\/(res)) => complete(Conflict, res.toString)
            case Failure(_)        => complete(NotFound)
            case msg               => complete(NotFound, s"There was an issue with the request: [$msg]")
          }
        }
      }
    }
  }

}