package krax.rest.routing

import akka.actor.ActorSystem
import akka.util.Timeout
import akka.stream.ActorMaterializer
import akka.http.scaladsl.server.Route

import krax.rest.routing.user.UserService

trait Services {
    implicit val system: ActorSystem
    implicit val materializer: ActorMaterializer
    implicit val timeout: Timeout

    def services: UserService

    val route: Route = services.usersRoute
}