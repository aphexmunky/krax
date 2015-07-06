import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

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

class MyServiceActor extends Actor with MyService with BackendCall {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

case object GetEmail
case class GetEmailResponse(email: Option[String])

case class Register(username: String, email: String)
case class Registered(username: String, email: String)

trait BackendCall {
	this: Actor =>

	import akka.cluster.routing.ClusterRouterGroup
	import akka.cluster.routing.ClusterRouterGroupSettings
	import akka.cluster.routing.AdaptiveLoadBalancingGroup
	import akka.cluster.routing.HeapMetricsSelector

	val backend = context.actorOf(
	  ClusterRouterGroup(AdaptiveLoadBalancingGroup(HeapMetricsSelector),
	    ClusterRouterGroupSettings(
	      totalInstances = 100, routeesPaths = List("/user/userService"),
	      allowLocalRoutees = true, useRole = Some("backend"))).props(),
	  name = "backendRouter2")
}
trait MyService extends HttpService {
  this: BackendCall =>

  val myRoute =
    path(Rest) { username =>
      get {
        respondWithMediaType(`text/html`) { // XML is marshalled to `text/xml` by default, so we simply override here
          complete {
          	def call = backend ! Register(username, s"${username}@gmail.com")
          	call
          	
            <html>
              <body>
                <h1>Say hello to <i>spray-routing</i> on <i>spray-can</i>!</h1>
              </body>
            </html>
          }
        }
      }
    }
}