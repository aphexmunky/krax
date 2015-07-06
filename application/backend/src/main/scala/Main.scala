import akka.actor.{ ActorSystem, Props, PoisonPill }
import akka.routing.FromConfig
import com.typesafe.config.ConfigFactory

import akka.contrib.pattern.{ ClusterSingletonManager, ClusterSingletonProxy }
import scala.concurrent.duration._

import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

	val config = ConfigFactory.load

	val system = ActorSystem("krax", config)

	val workRouter = system.actorOf(FromConfig.props(Props[Test2]), name = "workerRouter")

    system.actorOf(ClusterSingletonManager.props(
      singletonProps = Props(new Test(workRouter)), singletonName = "test",
      terminationMessage = PoisonPill, role = Some("backend")),
      name = "singleton")

    val singleton = system.actorOf(ClusterSingletonProxy.props(singletonPath = "/user/singleton/test", role = Some("backend")), name = "testProxy")

    // val testActor = system.actorOf(Props[Test], "test")

    // Thread.sleep(6000)

}

import akka.actor.{ Actor, ActorLogging, ActorRef }
import akka.routing.ConsistentHashingRouter.ConsistentHashable

case class Msg(s: String) extends ConsistentHashable {
	override def consistentHashKey: Any = s
}

class Test(otherActor: ActorRef) extends Actor with ActorLogging {

	log.info("!!! ********* Test Actor Started ********* !!!")
    context.system.scheduler.schedule(6 seconds, 2 second)(self ! "testing 123")

	def receive = {
		case msg => otherActor forward Msg(msg.toString)
	}
}

class Test2 extends Actor with ActorLogging {
	log.info("!!! ********* Test2 Actor Started ********* !!!")
	def receive = {
		case Msg(msg) => log.info(s"********* test2 received: [$msg] from: [$sender] *********")
	}
}