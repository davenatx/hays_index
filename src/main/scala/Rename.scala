package com.austindata

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.{ StaticQuery => Q }

import java.sql.Date

case class RenameRecord(recordType: String, documentType: String, volume: String, page: String, fileDate: Date, fileName: String)

object Rename extends App {

  database withSession { implicit session =>

    val query = Q.query[String, (String, String, String, String, Date, String)]("""
      SELECT DISTINCT RECTYP, DOCTYP, VOLUME, PAGE, FILEDATE, FNAME 
      FROM INDEX_RECORDS 
      WHERE EXTRACT(YEAR FROM FILEDATE) = ? AND RECTYP = 'OPR' ORDER BY VOLUME, PAGE, FILEDATE 
    """)

    println(query("1920").list)

  }
}