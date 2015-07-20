package krax.rest.routing

import akka.actor.Actor
import akka.util.Timeout
import scala.concurrent.duration._
import scala.language.postfixOps

trait BackendCall {
    this: Actor =>

    import akka.cluster.routing.ClusterRouterGroup
    import akka.cluster.routing.ClusterRouterGroupSettings
    import akka.cluster.routing.AdaptiveLoadBalancingGroup
    import akka.cluster.routing.HeapMetricsSelector

    implicit val timeout = Timeout(3 seconds)

    val backend = context.actorOf(
      ClusterRouterGroup(AdaptiveLoadBalancingGroup(HeapMetricsSelector),
        ClusterRouterGroupSettings(
          totalInstances = 100, routeesPaths = List("/user/userService"),
          allowLocalRoutees = true, useRole = Some("backend"))).props(),
      name = "backendRouter")
}