package com.mint.choco.repositories

import com.mint.choco.models.{Name, User}

trait UserStorage {
  def findUser(name: Name): User
}
