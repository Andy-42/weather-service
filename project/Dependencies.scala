import sbt.*

object Dependencies {

  object Org {
    val typelevel = "org.typelevel"
    val http4s = "org.http4s"
    val pureconfig = "com.github.pureconfig"
    val circe = "io.circe"
    val scalactic = "org.scalactic"
    val scalatest = "org.scalatest"
    val slf4j = "org.slf4j"
  }

  object Version {
    val http4s = "1.0.0-M39"
    val pureconfig = "0.17.4"
    val log4cats = "2.5.0"
    val circe = "0.14.5"
    val catsEffect = "3.5.0"
    val scalactic = "3.2.16"
    val scalatest = "3.2.16"
    val slf4j = "2.0.5"
  }

  lazy val typelevel: Seq[ModuleID] = Seq(
    Org.typelevel %% "cats-effect" % Version.catsEffect,
    Org.typelevel %% "log4cats-slf4j" % Version.log4cats,
  )

  lazy val pureconfig: Seq[ModuleID] = Seq(
    Org.pureconfig %% "pureconfig" % Version.pureconfig,
    Org.pureconfig %% "pureconfig-cats-effect" % Version.pureconfig,
  )

  lazy val http4s: Seq[ModuleID] = Seq(
    Org.http4s %% "http4s-ember-client" % Version.http4s,
    Org.http4s %% "http4s-ember-server" % Version.http4s,
    Org.http4s %% "http4s-circe" % Version.http4s,
    Org.http4s %% "http4s-dsl" % Version.http4s,
  )

  lazy val circe: Seq[ModuleID] = Seq(
    Org.circe %% "circe-core" % Version.circe,
    Org.circe %% "circe-parser" % Version.circe,
    Org.circe %% "circe-generic" % Version.circe,
  )

  lazy val test: Seq[ModuleID] = Seq(
    Org.scalactic %% "scalactic" % Version.scalactic % Test,
    Org.scalatest %% "scalatest" % Version.scalatest % Test
  )

  lazy val logging: Seq[ModuleID] = Seq(
    Org.slf4j % "slf4j-simple" % Version.slf4j,
  )

  lazy val all: Seq[ModuleID] = typelevel ++ pureconfig ++ http4s ++ circe ++ test ++ logging
}
