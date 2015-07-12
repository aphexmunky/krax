package krax.backend.user

import akka.actor.{ Actor, ActorLogging, ActorRef, Props }

import akka.contrib.pattern._

import krax.domain.User._

class UserService extends Actor with ActorLogging {

    def receive = {
        case registration: Register         ⇒ user forward registration
        case getEmail: GetEmail             ⇒ user forward getEmail
        case add: AddEmail                  ⇒ user forward add
    }

    val idExtractor: ShardRegion.IdExtractor = {
        case msg @ Register(username, _)    ⇒ (username, msg)
        case get @ GetEmail(username)       ⇒ (username, get)
        case add @ AddEmail(username, _, _) ⇒ (username, add)
    }

    val shardResolver: ShardRegion.ShardResolver = {
        case Register(username, _)          ⇒ (username.hashCode % 12).toString
        case GetEmail(username)             ⇒ (username.hashCode % 12).toString
        case AddEmail(username, _, _)       ⇒ (username.hashCode % 12).toString
    }

    val user: ActorRef = ClusterSharding(context.system).start(
        typeName = "User",
        entryProps = Some(Props[User]),
        idExtractor = idExtractor,
        shardResolver = shardResolver)
}