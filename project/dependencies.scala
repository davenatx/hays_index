import sbt._
import sbt.Keys._

object Dependencies {
  // Versions
  val scalaLoggingVersion = "3.1.0"
  val logbackVersion = "1.1.2"
  val configVersion = "1.2.1"
  val slickVersion = "2.1.0"
  val h2Version = "1.4.182"
  val hikariCPVersion = "2.2.5"

  // Libraries
  val scalaLogging =  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val config = "com.typesafe" % "config" % configVersion
  val slick = "com.typesafe.slick" %% "slick" % slickVersion
  val h2 = "com.h2database" % "h2" % h2Version
  val hikariCP = "com.zaxxer" % "HikariCP-java6" % hikariCPVersion
  
  // Projects
  val haysIndexDependencies = Seq(scalaLogging, logback, config, slick, h2, hikariCP)
}