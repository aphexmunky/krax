package krax.backend.user

import akka.actor.{ ActorLogging, ActorRef }
import akka.persistence.PersistentActor

import akka.contrib.pattern._

import krax.domain.User._

class User extends PersistentActor with ActorLogging {
	import ShardRegion.Passivate

	override def persistenceId = self.path.parent.name + "-" + self.path.name

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