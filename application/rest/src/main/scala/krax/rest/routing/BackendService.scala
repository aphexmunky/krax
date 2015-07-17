package krax.rest.routing

// import akka.actor.Actor
// import akka.util.Timeout
// import scala.language.postfixOps

// trait BackendService extends Services {

//     import akka.cluster.routing.ClusterRouterGroup
//     import akka.cluster.routing.ClusterRouterGroupSettings
//     import akka.cluster.routing.AdaptiveLoadBalancingGroup
//     import akka.cluster.routing.HeapMetricsSelector

//     val backend = system.actorOf(
//       ClusterRouterGroup(AdaptiveLoadBalancingGroup(HeapMetricsSelector),
//         ClusterRouterGroupSettings(
//           totalInstances = 100, routeesPaths = List("/user/userService"),
//           allowLocalRoutees = true, useRole = Some("backend"))).props(),
//       name = "backendRouter")
// }