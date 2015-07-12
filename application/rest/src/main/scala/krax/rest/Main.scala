package krax.rest

import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory
import akka.io.IO
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

import krax.rest.routing.Services

object Main extends App {

    val config = ConfigFactory.load

    implicit val system = ActorSystem("krax", config)
    implicit val timeout = Timeout(5 seconds)

    val service = system.actorOf(Props[Services], "rest-service")

    IO(Http) ? Http.Bind(service, interface = "0.0.0.0", port = 8080)
}
