package com

import com.typesafe.config.ConfigFactory

import scala.slick.driver.H2Driver.simple._

import com.zaxxer.hikari.HikariDataSource
import com.zaxxer.hikari.HikariConfig

import javax.sql.DataSource

package object austindata {
  private val config = ConfigFactory.load("settings.properties")

  /* Program Properties */
  lazy val yearToProcess = config.getString("yearToProcess")
  lazy val inputDirectory = config.getString("inputDirectory")

  /* DB Properties */
  lazy val dbUrl = config.getString("dbUrl")
  lazy val dbDriver = config.getString("dbDriver")
  lazy val indexFile = config.getString("indexFile")

  val dataSource = {
    val config = new HikariConfig

    config.setDriverClassName(dbDriver)
    config.setJdbcUrl(dbUrl)
    config.setUsername("sa")
    config.setPassword("")
    config.setMaximumPoolSize(20)
    config.setMinimumIdle(10)

    new HikariDataSource(config)
  }

  val database = Database.forDataSource(dataSource)
}