import akka.actor.ActorSystem

object Main extends App {
	val system = ActorSystem("krax")
	println("application started")

	Thread.sleep(5000)
	system.shutdown
}