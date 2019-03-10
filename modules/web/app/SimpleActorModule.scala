import com.google.inject.AbstractModule
import controllers.{ConfiguredActor, ConfiguredChildActor, ParentActor}
import play.api.libs.concurrent.AkkaGuiceSupport

class SimpleActorModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bindActor[ConfiguredActor]("configured-actor")

    bindActor[ParentActor]("parent-actor")
    bindActorFactory[ConfiguredChildActor, ConfiguredChildActor.Factory]
  }
}
