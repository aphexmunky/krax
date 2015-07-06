import akka.actor.{ Actor, ActorLogging }

class Test extends Actor with ActorLogging {
	log.info("*** test actor created ***")

	def receive = {
		case ClusterMessage(msg) => log.info(s"this is a test print - received $msg")
	}
}