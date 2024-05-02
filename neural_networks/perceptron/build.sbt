import scala.collection.immutable.Seq

ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.3.3"

lazy val root = (project in file("."))
  .settings(
    name := "perceptron",
    idePackagePrefix := Some("cipn")
  )

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "ujson" % "1.4.2",
  "com.lihaoyi" %% "os-lib" % "0.7.8"
)