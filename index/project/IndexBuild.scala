import sbt._
import sbt.Keys._

object IndexBuild extends Build {

  lazy val index = Project(
    id = "index",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "index",
      organization := "esc",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.9.2",
      resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
      libraryDependencies += "com.typesafe.akka" % "akka-actor" % "2.0.1"
    )
  )
}
