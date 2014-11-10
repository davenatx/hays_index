import sbt._
import sbt.Keys._

object Dependencies {
  // Versions
  val scalaLoggingVersion = "3.1.0"
  val logbackVersion = "1.1.2"
  val configVersion = "1.2.1"
    
  // Libraries
  val scalaLogging =  "com.typesafe.scala-logging" %% "scala-logging" % scalaLoggingVersion
  val logback = "ch.qos.logback" % "logback-classic" % logbackVersion
  val config = "com.typesafe" % "config" % configVersion
  
  // Projects
  val haysIndexDependencies = Seq(scalaLogging, logback, config)
}