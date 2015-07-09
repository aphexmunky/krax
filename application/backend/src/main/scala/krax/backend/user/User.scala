package krax.backend.user

import akka.actor.{ ActorLogging, ActorRef }
import akka.persistence.PersistentActor

import akka.contrib.pattern._

import krax.domain.User._

import scalaz._

class User extends PersistentActor with ActorLogging {
	import ShardRegion.Passivate

	override def persistenceId = self.path.parent.name + "-" + self.path.name

	def receiveCommand: Receive = unregistered

	def unregistered: Receive = {
		case Register(username) 		=> persist(RegisteredUser(username)) { evt =>
			log.info(s"successfully registered $username")
			sender ! \/-(evt)
			context.become(registered)
		}
		case AddEmail(username, email) 	=> {
			log.info(s"this user [$username] doesn't exist to add email [$email]")
			sender ! -\/(noUserToUpdate)
		}
	}

	def registered: Receive = {
		case Register(username)			=> {
			log.info(s"double registration attempted for user $username")
			sender ! -\/(alreadyRegistered)
		}
		case AddEmail(username, email)	=> persist(EmailAdded(email)) { evt =>
			log.info(s"successfully updated email for user $username to $email")
			sender ! \/-(evt)
		}
	}

	def receiveRecover: Receive = {
		case RegisteredUser(username) 	=> {
			log.info(s"recovering $username")
			context.become(registered)
		}
	}
}