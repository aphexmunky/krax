import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.{ Success, Failure }

import krax.domain.User._

object Main extends App {

	val config = ConfigFactory.load

	implicit val system = ActorSystem("krax", config)
	implicit val timeout = Timeout(5 seconds)

	val service = system.actorOf(Props[MyServiceActor], "demo-service")

	IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 8080)
}

import akka.actor.Actor
import spray.routing._
import spray.http._
import MediaTypes._
import StatusCodes._

class MyServiceActor extends Actor with MyService with BackendCall {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

trait BackendCall {
	this: Actor =>

	import akka.cluster.routing.ClusterRouterGroup
	import akka.cluster.routing.ClusterRouterGroupSettings
	import akka.cluster.routing.AdaptiveLoadBalancingGroup
	import akka.cluster.routing.HeapMetricsSelector

  implicit val timeout = Timeout(3 seconds)

	val backend = context.actorOf(
	  ClusterRouterGroup(AdaptiveLoadBalancingGroup(HeapMetricsSelector),
	    ClusterRouterGroupSettings(
	      totalInstances = 100, routeesPaths = List("/user/userService"),
	      allowLocalRoutees = true, useRole = Some("backend"))).props(),
	  name = "backendRouter2")
}
trait MyService extends HttpService {
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