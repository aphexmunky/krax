package krax.backend.user

import akka.actor._

import akka.cluster.sharding._

import krax.domain.User._

class UserService(as: ActorSystem) extends Actor with ActorLogging {

    def receive = {
        case registration: Register         ⇒ user forward registration
        case getEmail: GetEmail             ⇒ user forward getEmail
        case add: AddEmail                  ⇒ user forward add
    }

    val idExtractor: ShardRegion.ExtractEntityId = {
        case msg @ Register(username, _)    ⇒ (username, msg)
        case get @ GetEmail(username)       ⇒ (username, get)
        case add @ AddEmail(username, _, _) ⇒ (username, add)
    }

    val shardResolver: ShardRegion.ExtractShardId = {
        case Register(username, _)          ⇒ (username.hashCode % 12).toString
        case GetEmail(username)             ⇒ (username.hashCode % 12).toString
        case AddEmail(username, _, _)       ⇒ (username.hashCode % 12).toString
    }

    // val user: ActorRef = ClusterSharding(context.system).start(
    //     typeName = "User",
    //     entryProps = Props[User],
    //     settings = ClusterShardingSettings(context.system),
    //     extractEntityId = idExtractor,
    //     extractShardId = shardResolver)
    val user: ActorRef = ClusterSharding(as).start(
        typeName = "User",
        entityProps = Props[User],
        settings = ClusterShardingSettings(as),
        extractEntityId = idExtractor,
        extractShardId = shardResolver)
}