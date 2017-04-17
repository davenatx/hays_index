import Dependencies._

name := "hays_index"

organization := "com.dmp"

version := "0.1"

scalaVersion := "2.11.4"

scalacOptions ++= Seq("-optimize", "-deprecation", "-feature")

libraryDependencies ++= haysIndexDependencies

git.baseVersion := "0.1"

//versionWithGit

showCurrentGitBranch

scalariformSettings

org.scalastyle.sbt.ScalastylePlugin.Settings