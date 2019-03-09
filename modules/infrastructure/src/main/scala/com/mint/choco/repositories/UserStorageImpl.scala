package com.mint.choco.repositories

import com.mint.choco.models.{Age, Name, User}
import javax.inject.Singleton

@Singleton
class UserStorageImpl extends UserStorage {
  override def findUser(name: Name): User = {
    User(name, Age(19))
  }
}