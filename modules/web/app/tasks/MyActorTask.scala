package tasks

import java.time.LocalDateTime

import javax.inject._
import akka.actor.{Actor, ActorRef, ActorSystem, Props}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

/*
  Cron的な動作をするActor
 */
class MyActorTask @Inject()(actorSystem: ActorSystem, @Named("some-actor") someActor: ActorRef)(implicit ec: ExecutionContext) {

  actorSystem.scheduler.schedule(
    initialDelay = 0 microseconds,
    interval = 1 seconds,
    receiver = someActor,
    message = "tick"
  )

}

object SomeActor {
  def props = Props[SomeActor]
}

class SomeActor extends Actor {
  override def receive: Receive = {
    case "tick" =>
      println(s"boon boon boon: ${LocalDateTime.now()}")
  }
}