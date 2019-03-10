package tasks


import com.google.inject.AbstractModule
import play.api.libs.concurrent.AkkaGuiceSupport

class TasksModule extends AbstractModule with AkkaGuiceSupport {
  override def configure(): Unit = {
    bind(classOf[MyActorTask]).asEagerSingleton()
    bindActor[SomeActor]("some-actor")
  }
}