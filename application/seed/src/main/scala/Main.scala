import akka.actor.{ ActorSystem, Props }
import com.typesafe.config.ConfigFactory

object Main extends App {

    val config = ConfigFactory.load

    val system = ActorSystem("krax", config)

    system.actorOf(Props[SimpleClusterListener], name = "clusterListener")

}