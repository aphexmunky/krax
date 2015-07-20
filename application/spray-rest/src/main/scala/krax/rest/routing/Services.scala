package krax.rest.routing

import akka.actor.Actor
import krax.rest.routing.user.UserService

class Services extends Actor with UserService with BackendCall {

  // the HttpService trait defines only one abstract member, which
  // connects the services environment to the enclosing actor or test
  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(usersRoute)
}
