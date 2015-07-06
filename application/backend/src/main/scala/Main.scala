import akka.actor.{ ActorSystem, Props, PoisonPill }
import com.typesafe.config.ConfigFactory

import akka.contrib.pattern.{ ClusterSingletonManager, ClusterSingletonProxy }
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

	val config = ConfigFactory.load

	val system = ActorSystem("krax", config)

	val user1 = system.actorOf(Props(new User("heskethj")))
	val user2 = system.actorOf(Props(new User("conroya")))

	user1 ! Register("joshua.hesketh@thehutgroup.com")
	user2 ! Register("andy.conroy@thehutgroup.com")

}

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

case object GetEmail
case class GetEmailResponse(email: Option[String])

case class Register(email: String)
case class Registered(email: String)

class User(username: String) extends PersistentActor with ActorLogging {

	override def persistenceId = s"user/$username"

	var savedEmail: Option[String] = None

	def receiveCommand = {
		case GetEmail => sender ! GetEmailResponse(savedEmail)
		case Register(email) => persist(Registered(email)) { evt =>
			log.info(s"successfully registered $username as $email")
			savedEmail = Some(email)
		}
	}

	def receiveRecover = {
		case Registered(email) => savedEmail = Some(email)
	}
}