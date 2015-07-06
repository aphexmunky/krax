import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory

import akka.routing.FromConfig
import akka.cluster.routing.ClusterRouterGroup
import akka.cluster.routing.ClusterRouterGroupSettings
import akka.routing.ConsistentHashingGroup

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Main extends App {

	val config = ConfigFactory.load

	val system = ActorSystem("krax", config)

	system.actorOf(Props[SimpleClusterListener], name = "clusterListener")

	val backend = system.actorOf(FromConfig.props(), name = "factorialBackendRouter")

	Thread.sleep(6000)
	println("SENDING NOW!!! to: " + backend.toString)

	system.scheduler.schedule(10 seconds, 5 seconds)(backend ! ClusterMessage("print"))

	println("SENT!!!")
}