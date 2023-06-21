ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.11"

ThisBuild / scalacOptions := Seq("-deprecation")

lazy val root = (project in file("."))
  .settings(
    name := "weather-service",
    organization := "com.andy42",
    libraryDependencies ++= Dependencies.all
  )