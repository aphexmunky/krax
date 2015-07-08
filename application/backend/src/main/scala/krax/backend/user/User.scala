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
	}

	def registered: Receive = {
		case Register(username)			=> {
			log.info("double registration attempted for user $username")
			sender ! -\/(alreadyRegistered)
		}
	}

	def receiveRecover: Receive = {
		case RegisteredUser(username) 	=> {
			log.info(s"recovering $username")
			context.become(registered)
		}
	}
}