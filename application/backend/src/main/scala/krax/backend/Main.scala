package krax.backend

import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorSystem, Props }

import krax.backend.user.UserService

object Main extends App {

    val config = ConfigFactory.load

    val system = ActorSystem("krax", config)

    val userService = system.actorOf(Props(new UserService(system)), "userService")
}
