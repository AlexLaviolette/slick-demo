name := """demoapp"""
organization := "com.test"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.12.3"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies += cacheApi
libraryDependencies += ehcache
libraryDependencies += guice
libraryDependencies += ws
libraryDependencies += specs2 % Test

libraryDependencies += "com.h2database" % "h2" % "1.4.196"
libraryDependencies += "com.typesafe.play" %% "play-slick" % "3.0.3"
libraryDependencies += "com.typesafe.slick" %% "slick" % "3.2.3"
libraryDependencies += "com.typesafe.play" %% "play-slick-evolutions" % "3.0.3"
libraryDependencies += "mysql" % "mysql-connector-java" % "5.1.41"
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test
libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.2.20"
libraryDependencies += "com.github.karelcemus" %% "play-redis" % "2.3.0"
libraryDependencies += "com.twitter" %% "chill-akka" % "0.9.2"


testOptions in Test += Tests.Argument(TestFrameworks.ScalaTest, "-oF") // This enables full stack trace logging for testing http://www.scalatest.org/user_guide/using_scalatest_with_sbt
