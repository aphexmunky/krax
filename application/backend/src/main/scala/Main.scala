import akka.actor.{ ActorSystem, Props, PoisonPill }
import com.typesafe.config.ConfigFactory

import akka.contrib.pattern.{ ClusterSingletonManager, ClusterSingletonProxy }
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

	val config = ConfigFactory.load

	val system = ActorSystem("krax", config)

    system.actorOf(ClusterSingletonManager.props(
      singletonProps = Props[Test], singletonName = "test",
      terminationMessage = PoisonPill, role = Some("backend")),
      name = "singleton")

    val testActor = system.actorOf(ClusterSingletonProxy.props(singletonPath = "/user/singleton/test",
    role = Some("backend")), name = "testProxy")

    Thread.sleep(6000)
    system.scheduler.schedule(6 seconds, 1 second)(testActor ! "testing 123")

}

import akka.actor.{ Actor, ActorLogging }

class Test extends Actor with ActorLogging {
	log.info("!!! ********* Test Actor Started ********* !!!")
	def receive = {
		case msg => log.info(s"********* test received: [$msg] *********")
	}
}