package controllers

import akka.actor.ActorSystem
import akka.stream.Materializer
import javax.inject.Inject
import play.api.libs.streams.ActorFlow
import play.api.mvc.{AbstractController, ControllerComponents, WebSocket}

class MyWSController @Inject()(cc: ControllerComponents)(implicit system: ActorSystem, mat: Materializer) extends AbstractController(cc) {

  def socket = WebSocket.accept[String, String] { request =>
    ActorFlow.actorRef {out =>
      MyWebSocketActor.props(out)
    }
  }
}


import akka.actor._

object MyWebSocketActor {
  def props(out: ActorRef) = Props(new MyWebSocketACtor(out))
}

class MyWebSocketACtor(out: ActorRef) extends Actor {
  override def receive: Receive = {
    case msg: String =>
      out ! s"I received your message: $msg"
  }
}