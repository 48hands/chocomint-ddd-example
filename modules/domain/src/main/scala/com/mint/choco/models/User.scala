package com.mint.choco.models

case class Name(first: String, last: String)
case class Age(age: Int)
case class User(name: Name, age: Age)