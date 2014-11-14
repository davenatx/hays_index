package com.austindata

import scala.slick.driver.H2Driver.simple._

import java.sql.Date

case class IndexRecord(fileName: String, party1: String, party2: String, documentType: String, recordType: String, volume: String, page: String,
  fileDate: Date, rest: String, id: Option[Int] = None)

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
  def recordTypeIndex = index("IDX_RECTYP", recordType, unique = true)
  def volumeIndex = index("IDX_VOLUME", volume, unique = true)
  def fileDateIndex = index("IDX_FILEDATE", fileDate, unique = true)
}