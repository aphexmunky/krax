package krax.backend.user

import akka.actor.{ ActorLogging, ActorRef }
import akka.persistence.PersistentActor

import akka.contrib.pattern._

import krax.domain.User._
import krax.rest.Request._

import scalaz._

class User extends PersistentActor with ActorLogging {
	import ShardRegion.Passivate

	override def persistenceId = self.path.parent.name + "-" + self.path.name

	def receiveCommand: Receive = unregistered

	def unregistered: Receive = {
		case Register(username, rq)			=> persist(RegisteredUser(username)) { evt =>
			log.info(s"successfully registered $username against ip: ${rq.ip}")
			sender ! \/-(evt)
			context.become(registered)
		}
		case AddEmail(username, email, rq) 	=> {
			log.info(s"user [$username] (ip: ${rq.ip}) doesn't exist to add email [$email]")
			sender ! -\/(noUserToUpdate)
		}
	}

	def registered: Receive = {
		case Register(username, rq)			=> {
			log.info(s"double registration attempted for user $username by ip: ${rq.ip}")
			sender ! -\/(alreadyRegistered)
		}
		case AddEmail(username, email, rq)	=> {
			persist(EmailAdded(email)) { evt =>
				log.info(s"successfully updated email for user $username to $email by ip: ${rq.ip}")
				sender ! \/-(evt)
			}
		}
	}

	def receiveRecover: Receive = {
		case RegisteredUser(username) 		=> {
			log.info(s"recovering $username")
			context.become(registered)
		}
		case AddEmail(_ email) 				=> {
			log.info(s"recovering $email")
		}
	}
}