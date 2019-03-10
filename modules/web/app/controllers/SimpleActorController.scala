package controllers

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import controllers.HelloActor.SayHello
import javax.inject.{Inject, Named}
import play.api.mvc.{AbstractController, ControllerComponents}

import scala.concurrent.duration._
import akka.pattern.ask
import akka.util.Timeout
import com.google.inject.assistedinject.Assisted
import controllers.ConfiguredActor.GetConfig
import controllers.ParentActor.GetChild
import play.api.libs.concurrent.InjectedActorSupport

import scala.concurrent.{Await, ExecutionContext, Future}

class SimpleActorController @Inject()(@Named("configured-actor") configuredActor: ActorRef,
                                      @Named("parent-actor") parentActor: ActorRef,
                                      cc: ControllerComponents)
                                     (implicit system: ActorSystem, ec: ExecutionContext) extends AbstractController(cc) {

  implicit val timeout: Timeout = 5 seconds

  val helloActor = system.actorOf(HelloActor.props)

  def sayHello(name: String) = Action.async {
    (helloActor ? SayHello(name)).mapTo[String].map { message =>
      Ok(message)
    }
  }

  def getConfig = Action.async {
    (configuredActor ? GetConfig).mapTo[String].map { message =>
      Ok(message)
    }
  }

  def getChildActor = Action.async {
    import ConfiguredChildActor._
    (parentActor ? GetChild("tekito")).mapTo[ActorRef].map { childActor =>
      childActor ! GetConfig2
      Ok(childActor.toString())
    }
  }

}

object HelloActor {

  def props = Props[HelloActor]

  case class SayHello(name: String)

}

class HelloActor extends Actor {
  override def receive: Receive = {
    case SayHello(name) =>
      sender() ! s"Hello, $name"
  }
}

/*
  Dependency injecting actors
 */

import play.api.Configuration


object ConfiguredActor {

  case object GetConfig

}

// このアクターをSimpleActorControllerにDIする。
// ただし、このアクターはルートアクター
class ConfiguredActor @Inject()(configuration: Configuration) extends Actor {

  import ConfiguredActor._

  val config = configuration.getOptional[String]("my.config").getOrElse("none")

  override def receive: Receive = {
    case GetConfig =>
      sender() ! config
  }
}

/*
 * ここからは子アクターをDIするサンプル
 */

object ConfiguredChildActor {

  case object GetConfig2

  // このファクトリでActorを返すようになっている。
  // このActorをDIによってbindしてConfiguredActorを返すようにする。
  trait Factory {
    def apply(key: String): Actor
  }

}

class ConfiguredChildActor @Inject()(configuration: Configuration,
                                     @Assisted key: String) // Assistedは手動で提供
  extends Actor {

  import ConfiguredActor._

  val config = configuration.getOptional[String](key).getOrElse("none none none")

  override def receive: Receive = {
    case GetConfig =>
      sender() ! config
  }
}

// 親アクターはルートアクターである。
object ParentActor {

  case class GetChild(key: String)

}

class ParentActor @Inject()(childFactory: ConfiguredChildActor.Factory)
  extends Actor with InjectedActorSupport {

  import ParentActor._

  override def receive: Receive = {
    case GetChild(key) =>
      // injectedChildによって、子アクターを生成する
      val child: ActorRef = injectedChild(childFactory(key), key)
      // senderに子アクターを投げる。
      sender() ! child
  }
}