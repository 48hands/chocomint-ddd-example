import Dependencies._

lazy val commonSettings = Seq(
  scalaVersion := "2.12.7",
  organization := "com.mint.choco",
  version := "0.1.0",
  libraryDependencies ++= CommonDepends
)

lazy val dddOnScala = (project in file("."))
  .aggregate(domain, infra, application, web)
  .settings(
    name := "chocomint-dddOnScala",
    commonSettings,
  )

lazy val domain = (project in file("modules/domain"))
  .settings(
    name := "chocomint-domain",
    commonSettings,
  )

lazy val infra = (project in file("modules/infrastructure"))
  .settings(
    name := "chocomint-infrastructure",
    commonSettings,
    libraryDependencies ++= InfraDepends,
    libraryDependencies += guice,
  )
  .dependsOn(domain)

lazy val application = (project in file("modules/application"))
  .settings(
    name := "chocomint-application",
    commonSettings,
    libraryDependencies += guice,
  )
  .dependsOn(domain)


lazy val web = (project in file("modules/web"))
  .enablePlugins(PlayScala, DockerPlugin)
  .settings(
    name := "chocomint-web",
    version := "1.0",
    commonSettings,
    libraryDependencies ++= Seq(jdbc, ehcache, ws, specs2 % Test, guice),
  )
  .settings(
    // Dockerのimageにパッケージング化するための設定
    dockerBaseImage := "java:openjdk-8-jdk", // image生成元のimage
    dockerRepository := Some("chocomint-kusoyaro"), // レジストリ名
    dockerExposedPorts := Seq(9000) // 公開するport
  )
  .dependsOn(application)
