import sbt._

object Dependencies {
  lazy val CommonDepends = Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  )

  lazy val InfraDepends = Seq(
    "org.scalikejdbc" %% "scalikejdbc" % "3.0.2",
    "org.scalikejdbc" %% "scalikejdbc-config"  % "3.0.2",
    "com.h2database"  %  "h2"                % "1.4.196",
    "org.scalikejdbc" %% "scalikejdbc-test" % "3.0.2" % Test
  )

  lazy val WebDependes = Seq(
    "com.typesafe.play" %% "play-json" % "2.6.9"
  )
}