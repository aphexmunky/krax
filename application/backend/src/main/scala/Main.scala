import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory

import akka.cluster.routing.ClusterRouterPool
import akka.cluster.routing.ClusterRouterPoolSettings
import akka.routing.ConsistentHashingPool

object Main extends App {

	val config = ConfigFactory.load

	val system = ActorSystem("krax", config)

    val workerRouter = system.actorOf(
      ClusterRouterPool(ConsistentHashingPool(0), ClusterRouterPoolSettings(
        totalInstances = 100, maxInstancesPerNode = 3,
        allowLocalRoutees = false, useRole = Some("backend"))).props(Props[Test]),
      name = "test")

}