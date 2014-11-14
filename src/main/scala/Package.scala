package com

import com.typesafe.config.ConfigFactory

import scala.slick.driver.H2Driver.simple._

package object austindata {
  private val config = ConfigFactory.load("settings.properties")

  lazy val dbUrl = config.getString("dbUrl")
  lazy val dbDriver = config.getString("dbDriver")
  lazy val indexFileName = config.getString("indexFileName")

  def DB() = Database.forURL(dbUrl, driver = dbDriver)
}