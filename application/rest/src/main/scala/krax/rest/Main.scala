package krax.rest

import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory
import akka.stream.ActorMaterializer
import akka.http.scaladsl.Http
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps

import krax.rest.routing.Services
// import krax.rest.routing.user.UserService

object Main extends App with Services {

    val config = ConfigFactory.load

    implicit val system = ActorSystem("krax", config)
    implicit val materializer = ActorMaterializer()
    implicit val timeout = Timeout(5 seconds)

    val bindingFuture = Http().bindAndHandle(route, "0.0.0.0", 7000)

}
