package com.austindata

import scala.slick.driver.H2Driver.simple._

import java.sql.Date

object ImportIndex {

  def apply {
    // The query interface for the IndexRecords table
    val indexRecords: TableQuery[IndexRecords] = TableQuery[IndexRecords]

    Database.forURL("jdbc:h2:mem:hays_index", driver = "org.h2.Driver") withSession {
      implicit session =>

        indexRecords.ddl.create

        val indexRecord = IndexRecord("00000007", "SOUTHMAYD JOHN ALLEN", "JAMES ALFRED F", "WARRANTY DEED", "OPR", "A", "940", Date.valueOf("1850-02-23"), "006251846SEE INSTRUMENT")

        indexRecords += (indexRecord)

        println(indexRecords.list)
    }
  }

}