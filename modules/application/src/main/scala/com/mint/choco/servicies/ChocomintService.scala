package com.mint.choco.servicies

import com.mint.choco.models.{Age, Name, User}
import com.mint.choco.repositories.UserStorage
import javax.inject._

trait ChocomintService {
  def execute(): User
}

@Singleton
class ChocomintServiceImpl @Inject()(storage: UserStorage) extends ChocomintService {
  override def execute(): User = {
    storage.findUser(Name("baki", "hannma"))
  }
}