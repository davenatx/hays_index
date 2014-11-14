package com.austindata

import scala.slick.driver.H2Driver.simple._

import java.sql.Date

/* Case Class representing a parsed Index Record. */
case class IndexRecord(fileName: String, party1: String, party2: String, documentType: String, recordType: String, volume: String, page: String,
  fileDate: Date, rest: String, id: Option[Int] = None)

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
    DB() withSession { implicit session =>
      indexRecords.ddl.create
    }
  }

  /* Drop the tables */
  def dropTables {
    DB() withSession { implicit session =>
      indexRecords.ddl.drop
    }
  }

  /* Insert one IndexRecord into IndexRecords */
  def insert(record: IndexRecord) {
    DB() withSession { implicit session =>
      indexRecords += (record)
    }
  }
  /* Batch Insert a sequence of IndexRecord into IndexRecords */
  def insert(records: Seq[IndexRecord]) {
    DB() withSession { implicit session =>
      indexRecords ++= records
    }
  }
  //    val indexRecord = IndexRecord("00000007", "SOUTHMAYD JOHN ALLEN", "JAMES ALFRED F", "WARRANTY DEED", "OPR", "A", "940", Date.valueOf("1850-02-23"), "006251846SEE INSTRUMENT")
}