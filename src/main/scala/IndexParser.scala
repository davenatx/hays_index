package com.austindata

import scala.io.Source
import com.typesafe.scalalogging.LazyLogging
import java.sql.Date
import DBHelpers._

sealed trait RecordType
case object ThreeCharRecordType extends RecordType
case object TwoCharRecordType extends RecordType

object IndexParser extends LazyLogging {

  type Line = String

  // 8 in length
  val parseIndexId = (line: Line) => line.substring(0, 8)

  // 40 in length
  val parseParty1 = (line: Line) => line.substring(8, 48).trim

  // 40 in length
  val parseParty2 = (line: Line) => line.substring(48, 88).trim

  // 20 in length
  val parseDocumentType = (line: Line) => line.substring(88, 108).trim

  // RecordType is used to indicate to other formatting functions what position to use.  This is because record type can be two or three characters
  val parseRecordType = (line: Line) => {
    line.substring(110, 111).charAt(0).isLetter match {
      case false => (TwoCharRecordType, line.substring(108, 110))
      case true => (ThreeCharRecordType, line.substring(108, 111))
    }
  }

  // Decide how to format the volume becuase of possible alphas.  Maybe only at the end?
  val parseVolume = (line: Line, recordType: RecordType) => recordType match {
    case TwoCharRecordType => line.substring(110, 115)
    case ThreeCharRecordType => line.substring(111, 116)
  }

  val parsePage = (line: Line, recordType: RecordType) => recordType match {
    case TwoCharRecordType => line.substring(115, 119)
    case ThreeCharRecordType => line.substring(116, 120)
  }

  val parseFileDate = (line: Line, recordType: RecordType) => recordType match {
    case TwoCharRecordType => line.substring(119, 127)
    case ThreeCharRecordType => line.substring(120, 128)
  }

  val parseRest = (line: Line, recordType: RecordType) => recordType match {
    case TwoCharRecordType => line.substring(127, line.length).trim
    case ThreeCharRecordType => line.substring(128, line.length).trim
  }

  // Convert the date to a java.sql.Date
  private def formatDate(date: String) = {
    val month = date.substring(0, 2)
    val day = date.substring(2, 4)
    val year = date.substring(4, 8)

    Date.valueOf(year + "-" + month + "-" + day)
  }

  /**
   * Format the volume where it is 5 characters in length.  The assumption is if the volume
   * has a letter, it is the last position.
   */
  private def formatVolume(volume: String) = volume.charAt(volume.length - 1).isLetter match {
    case true => {
      val numeric = volume.substring(0, 4).toInt
      val alpha = volume.substring(4, 5)
      if (numeric == 0) alpha
      else numeric + alpha
    }
    case false => {
      volume.toInt.toString
    }
  }

  /**
   * Format the page where it is 4 characters in length.  The assumption is if the page
   * has a letter, it is the last position.
   */
  private def formatPage(page: String) = page.charAt(page.length - 1).isLetter match {
    case true => {
      val numeric = page.substring(0, 3).toInt
      val alpha = page.substring(3, 4)
      if (numeric == 0) alpha
      else numeric + alpha
    }
    case false => {
      page.toInt.toString
    }
  }

  /* Parse each line and insert into Database */
  private def parseLine(line: Line) {
    val indexId = parseIndexId(line)
    val party1 = parseParty1(line)
    val party2 = parseParty2(line)
    val documentType = parseDocumentType(line)
    val (recordType, recordTypeName) = parseRecordType(line)
    val volume = formatVolume(parseVolume(line, recordType))
    val page = formatPage(parsePage(line, recordType))
    val fileDate = formatDate(parseFileDate(line, recordType))
    val rest = parseRest(line, recordType)

    logger.info(s"Parsing IndexId: $indexId")

    insert(IndexRecord(indexId, party1, party2, documentType, recordTypeName, volume, page, fileDate, rest))

  }

  /* Setup Database Tables and parse the index file */
  def parse = {

    //dropTables

    createTables

    /* Parse the file in parallel */
    val iterator = Source.fromFile(indexFile).getLines.grouped(10000) //Source.fromURL(getClass.getResource("/" + indexFileName)).getLines.grouped(10000)
    iterator.foreach { lines =>
      lines.par.foreach { line => parseLine(line) }
    }
  }

  def apply() = parse
}

/**
 * Parse the Index and insert into the database
 */
object ImportIndex extends App {
  throw new RuntimeException("The index has already been imported!  This is here to prevent me from accidently running this application")
  IndexParser()
}