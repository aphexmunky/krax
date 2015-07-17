package krax.rest.routing

import akka.actor.ActorSystem
import akka.util.Timeout
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Route

import akka.http.scaladsl.server.Directives._
import krax.domain.User._
import krax.rest.Request._
import akka.pattern.ask
import scala.util.{ Success, Failure }
import akka.http.scaladsl.model.StatusCodes._
import scalaz.{-\/,\/-,\/}
// import krax.rest.routing.user.UserService

trait Services {
  implicit val system: ActorSystem
  implicit val materializer: ActorMaterializer
  implicit val timeout: Timeout

  import akka.cluster.routing.ClusterRouterGroup
  import akka.cluster.routing.ClusterRouterGroupSettings
  import akka.cluster.routing.AdaptiveLoadBalancingGroup
  import akka.cluster.routing.HeapMetricsSelector

  lazy val backend = system.actorOf(
    ClusterRouterGroup(AdaptiveLoadBalancingGroup(HeapMetricsSelector),
      ClusterRouterGroupSettings(
        totalInstances = 100, routeesPaths = List("/user/userService"),
        allowLocalRoutees = true, useRole = Some("backend"))).props(),
    name = "backendRouter")


  val route: Route = pathPrefix("users") {
    createUser ~ updateUser
  }

  def createUser = pathPrefix("register") {
    post {
      extractClientIP { ip =>
        formFields('username) { username =>
          val requestDetails = RequestDetails(Some(ip.toString))
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
  }

  def updateUser = pathPrefix("update" / Segment) { username =>
    pathPrefix("email") {
      extractClientIP { ip =>
        post {
          formFields('email) { email =>
            val requestDetails = RequestDetails(Some(ip.toString))
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
}