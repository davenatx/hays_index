package com.austindata

import scala.slick.driver.H2Driver.simple._
import scala.slick.jdbc.{ StaticQuery => Q }

import java.sql.Date

/* Domain Object representing a parsed Index Record */
case class IndexRecord(fileName: String, party1: String, party2: String, documentType: String, recordType: String, volume: String, page: String,
  fileDate: Date, rest: String, id: Option[Int] = None)

/* Domain object representing the fields returned by OPRRecordsByYear */
case class QueryRecord(recordType: String, documentType: String, volume: String, page: String, fileDate: Date, fileName: String)

/* Slick Table object.  The * projection has bi-directional mapping (<>) to IndexRecord */
class IndexRecords(tag: Tag)
    extends Table[IndexRecord](tag, "INDEX_RECORDS") {
  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
  def fileName = column[String]("FNAME")
  def party1 = column[String]("P1")
  def party2 = column[String]("P2")
  def documentType = column[String]("DOCTYP")
  def recordType = column[String]("RECTYP")
  def volume = column[String]("VOLUME")
  def page = column[String]("PAGE")
  def fileDate = column[Date]("FILEDATE")
  def rest = column[String]("REST")
  def * = (fileName, party1, party2, documentType, recordType, volume, page, fileDate, rest, id.?) <> (IndexRecord.tupled, IndexRecord.unapply)
  def recordTypeIndex = index("IDX_RECTYP", recordType, unique = false)
  def volumeIndex = index("IDX_VOLUME", volume, unique = false)
  def fileDateIndex = index("IDX_FILEDATE", fileDate, unique = false)
}

/* Helper ojbect to perform Database operations */
object DBHelpers {

  /* The query interface for the IndexRecords table */
  val indexRecords: TableQuery[IndexRecords] = TableQuery[IndexRecords]

  /* Create the tables */
  def createTables {
    database withSession { implicit session =>
      indexRecords.ddl.create
    }
  }

  /* Drop the tables */
  def dropTables {
    database withSession { implicit session =>
      indexRecords.ddl.drop
    }
  }

  /* Insert one IndexRecord into IndexRecords */
  def insert(record: IndexRecord) {
    database withSession { implicit session =>
      indexRecords += (record)
    }
  }
  /* Batch Insert a sequence of IndexRecord into IndexRecords */
  def insert(records: Seq[IndexRecord]) {
    database withSession { implicit session =>
      indexRecords ++= records
    }
  }

  /* Query OPR Records by Year.  This is a distinct query becuase records can be represented multiple times due to the number of parties, etc... */
  def OPRRecordsByYear(year: String): List[QueryRecord] = {
    database withSession { implicit session =>

      val query = Q.query[String, (String, String, String, String, Date, String)]("""
      SELECT DISTINCT RECTYP, DOCTYP, VOLUME, PAGE, FILEDATE, FNAME 
      FROM INDEX_RECORDS 
      WHERE EXTRACT(YEAR FROM FILEDATE) = ? AND RECTYP = 'OPR' ORDER BY VOLUME, PAGE, FILEDATE 
    """)

      query(year).list map (r => QueryRecord(r._1, r._2, r._3, r._4, r._5, r._6))
    }
  }
}