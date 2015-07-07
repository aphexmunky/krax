import akka.actor._
import com.typesafe.config.ConfigFactory

import akka.contrib.pattern._
import akka.pattern.ask
import akka.contrib.pattern.ShardRegion
import akka.util.Timeout
import scala.concurrent.duration._
import scala.util.Random

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

	val config = ConfigFactory.load

	val system = ActorSystem("krax", config)

	val userService = system.actorOf(Props[UserService], "userService")
}

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

case class GetEmail(username: String)
case class GetEmailResponse(email: Option[String])

case class Register(username: String, email: String)
case class Registered(username: String, email: String)

class UserService extends Actor with ActorLogging {
	import akka.cluster.routing.ClusterRouterGroup
	import akka.cluster.routing.ClusterRouterGroupSettings
	import akka.routing.ConsistentHashingGroup

	def receive = {
		case registration: Register => user forward registration
		case getEmail: GetEmail 	=> user forward getEmail
	}

	val idExtractor: ShardRegion.IdExtractor = {
		case msg @ Register(username, _)	⇒ (username, msg)
		case get @ GetEmail(username)		⇒ (username, get)
	}

	val shardResolver: ShardRegion.ShardResolver = {
		case Register(username, _)			⇒ (username.hashCode % 12).toString
		case GetEmail(username)				⇒ (username.hashCode % 12).toString
	}

	val user: ActorRef = ClusterSharding(context.system).start(
		typeName = "User",
		entryProps = Some(Props[User]),
		idExtractor = idExtractor,
		shardResolver = shardResolver)
}

class User extends PersistentActor with ActorLogging {
	import ShardRegion.Passivate

	override def persistenceId = self.path.parent.name + "-" + self.path.name
	println(s"!!!!!!!!!!!!!!!!!!!!! $persistenceId")

	var savedEmail: Option[String] = None

	def receiveCommand = {
		case get: GetEmail 				=> sender ! GetEmailResponse(savedEmail)
		case Register(username, email) 	=> persist(Registered(username, email)) { evt =>
			log.info(s"successfully registered $username as $email")
			savedEmail = Some(email)
		}
	}

	def receiveRecover = {
		case Registered(username, email) => {
			log.info(s"recovering $username, re-setting email to: [$email]")
			savedEmail = Some(email)
		}
	}
}