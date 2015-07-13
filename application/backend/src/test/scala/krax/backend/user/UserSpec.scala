package krax.backend.user

import akka.actor.{ ActorSystem, Actor, Props, PoisonPill }
import akka.testkit.{ TestActors, TestKit, ImplicitSender, TestActorRef }
import org.scalatest.WordSpecLike
import org.scalatest.Matchers
import org.scalatest.BeforeAndAfterAll
import scalaz._

import krax.domain.User._
import krax.rest.Request._
 
class UserSpec(_system: ActorSystem) extends TestKit(_system) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {
 
  def this() = this(ActorSystem("UserSpec"))
 
  override def afterAll {
    TestKit.shutdownActorSystem(system)
  }
 
  "A Persistent User" must {
 
    "Register a username" in {
      val user = system.actorOf(Props[User])
      user ! Register("name", RequestDetails("ip"))
      Thread.sleep(100)
      user ! PoisonPill
      Thread.sleep(100)
      system.actorOf(Props[User])
      expectMsgClass(classOf[\/-[RegisteredUser]])
    }
 
  }
}