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

	println("Sleeping for 20 seconds")
	Thread.sleep(20000)
	println("OK, i'm awake - let's do this")

	val system = ActorSystem("krax", config)

	println("Starting the scheduler")
	system.scheduler.schedule(10 seconds, 1 second)(send)

	val idExtractor: ShardRegion.IdExtractor = {
		case msg @ Register(username, _)	⇒ (username, msg)
	}

	val shardResolver: ShardRegion.ShardResolver = {
		case Register(username, _)			⇒ (username.hashCode % 12).toString
	}

	val user: ActorRef = ClusterSharding(system).start(
		typeName = "User",
		entryProps = Some(Props[User]),
		idExtractor = idExtractor,
		shardResolver = shardResolver)

	def send = {
		println("******************** CREATING AND SENDING ***********************")
		val acct = createRandomAccount
		user ! Register(acct.username, acct.email)
	}

	val firstNames = List("josh","andy","siva","alex","elliot","phil","frank")
	val surnames = List("hesketh","conroy","prakash","borshik","kennedy","parthiban")

	case class Account(username: String, email: String)
	def createRandomAccount: Account = {
		def grab[T](l: List[T]) = Random.shuffle(l).head
		val firstName = grab(firstNames)
		val surname = grab(surnames)
		val username = surname + firstName.head
		val email = s"${firstName}.${surname}@thehutgroup.com"
		Account(username, email)
	}
}

import akka.actor.ActorLogging
import akka.persistence.PersistentActor

case object GetEmail
case class GetEmailResponse(email: Option[String])

case class Register(username: String, email: String)
case class Registered(username: String, email: String)

class User extends PersistentActor with ActorLogging {
	import ShardRegion.Passivate

	override def persistenceId = self.path.parent.name + "-" + self.path.name
	println(s"!!!!!!!!!!!!!!!!!!!!! $persistenceId")

	var savedEmail: Option[String] = None

	def receiveCommand = {
		case GetEmail => sender ! GetEmailResponse(savedEmail)
		case Register(username, email) => persist(Registered(username, email)) { evt =>
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