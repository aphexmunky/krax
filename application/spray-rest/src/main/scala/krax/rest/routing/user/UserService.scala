package krax.rest.routing.user

import krax.rest.routing.BackendCall
import krax.domain.User._

import spray.routing.HttpService
import spray.http.StatusCodes._
import akka.pattern.ask
import scala.util.{ Success, Failure }

import krax.entities.UserEntities._
import krax.rest.Request._
import spray.httpx.SprayJsonSupport._
import scalaz._

trait UserService extends HttpService {
  this: BackendCall =>

  implicit def executionContext = actorRefFactory.dispatcher

  lazy val usersRoute = pathPrefix("users") {
    createUser ~ updateUser
  }

  def createUser = pathPrefix("register") {
    post {
      formFields('username) { username =>
        clientIP { ipAddr =>
          val requestDetails = RequestDetails(ipAddr.toString)
          val f = (backend ? Register(username, requestDetails)).mapTo[\/[RegistrationError, RegisteredUser]]
          onComplete(f) {
            case Success(\/-(res)) => complete(OK, res)
            case Success(-\/(res)) => complete(Conflict, res)
            case Failure(_)        => complete(NotFound)
            case msg               => complete(NotFound, s"There was an issue with the request: [$msg]")
          }
        }
      }
    }
  }

  def updateUser = pathPrefix("update" / Segment) { username =>
    pathPrefix("email") {
      post {
        formFields('email) { email =>
          clientIP { ipAddr =>
            val requestDetails = RequestDetails(ipAddr.toString)
            val f = (backend ? AddEmail(username, email, requestDetails)).mapTo[\/[UpdateError, EmailAdded]]
            onComplete(f) {
              case Success(\/-(res)) => complete(OK, res)
              case Success(-\/(res)) => complete(Conflict, res)
              case Failure(_)        => complete(NotFound)
              case msg               => complete(NotFound, s"There was an issue with the request: [$msg]")
            }
          }
        }
      }
    }
  }

}