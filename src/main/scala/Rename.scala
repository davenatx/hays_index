package com.austindata

import DBHelpers._
import com.typesafe.scalalogging.LazyLogging
import scala.annotation.tailrec

import java.sql.Date
import java.util.Calendar
import java.util.GregorianCalendar
import java.io.File
import java.io.FilenameFilter
import java.nio.file.Files
import java.nio.file.StandardCopyOption.ATOMIC_MOVE
import java.nio.file.Paths

/* A Record where the new filename has been generated */
case class RenameRecord(recordType: String, documentType: String, volume: String, page: String, fileDate: Date, fileName: String, newFileName: String)

/* FilenameFilter that returns all of the files begining with the fileName */
class MyFileFilter(filenamePrefix: String) extends FilenameFilter {
  def accept(directory: File, filename: String): Boolean = {
    if (filename.startsWith(filenamePrefix + "_")) true
    else false
  }
}

object RenameHelpers extends LazyLogging {

  /* Prepend 0 to volume or page up to length 3 */
  val padOnLeft = (s: String) => s.reverse.padTo(3, '0').reverse

  /* Retrieve year as String from java.sql.Date */
  val dateToYear = (date: Date) => {
    val cal = new GregorianCalendar
    cal.setTime(date)
    cal.get(Calendar.YEAR).toString
  }

  /* Check if the original file exits using the filename and appending _0001.TIF */
  val originalFileExists = (fileName: String) => {
    val currentFile = new File(inputDirectory + "/" + fileName + "_0001.TIF")
    if (currentFile.exists) true
    else false
  }

  /* Check if the new file exists using the filename and appending .001 */
  val newFileExists = (newFileName: String, recordType: String) => {
    val outputDir = new File(inputDirectory + "/" + recordType)
    if (!outputDir.exists) outputDir.mkdirs
    val newFile = new File(outputDir, newFileName + ".001")
    if (newFile.exists) true
    else false
  }

  /* Format the page extension from the original filename */
  val formatPageExtension = (filename: String) => {
    filename.split("_")(1).split("\\.")(0).substring(1)
  }

  // Use nio to move files
  def moveFile(sourceFile: String, destinationFile: String) {
    val nioSourceFile = Paths.get(sourceFile)
    val nioDestinationFile = Paths.get(destinationFile)
    Files.move(nioSourceFile, nioDestinationFile, ATOMIC_MOVE)
  }

  /* Transform QueryRecord to RenameRecord.  This transformatoin also creates the newFileName */
  def getRenameRecords(year: String): List[RenameRecord] = {
    OPRRecordsByYear(year) map (r => RenameRecord(
      r.recordType,
      r.documentType,
      r.volume,
      r.page,
      r.fileDate,
      r.fileName,
      dateToYear(r.fileDate) + padOnLeft(r.volume) + padOnLeft(r.page)
    ))
  }

  /* perform rename */
  def rename(newFileName: String, files: List[File], outputDir: String) {

    @tailrec
    def processFileRename(xs: List[File]): Unit = xs match {
      case List() =>
      case head :: tail => {
        val oldFile = head.getAbsolutePath
        val newFile = outputDir + "/" + newFileName + "." + formatPageExtension(head.getName)
        moveFile(oldFile, newFile)
        processFileRename(tail)
      }
    }

    processFileRename(files)
  }

  @tailrec
  def processRenameRecords(xs: List[RenameRecord]): Unit = xs match {
    case List() =>
    case head :: tail => {
      originalFileExists(head.fileName) match {
        case false => {
          logger.warn("Original File does not exists: " + head.fileName + " for Volume: " + head.volume + " ,Page: " + head.page)
          processRenameRecords(tail)
        }
        case true => {
          newFileExists(head.newFileName, head.recordType) match {
            case true => {
              logger.info("New File already exists in output directory! Appending an A to the end. Volume: " + head.volume + " ,Page: "
                + head.page + " ,Filename: " + head.fileName + " ,NewFilename: " + head.newFileName)

              // Duplicate rename record with "A" appending to the end of the newFileName.  Then Prepend to tail in recursive call
              val updatedRecord = head.copy(newFileName = head.newFileName + "A")
              processRenameRecords(updatedRecord :: tail)
            }
            case false => {
              // Perform renaming
              val outputDir = inputDirectory + "/" + head.recordType
              val files = new File(inputDirectory).listFiles(new MyFileFilter(head.fileName)).toList
              rename(head.newFileName, files, outputDir)
              // recursive call
              processRenameRecords(tail)
            }
          }
        }
      }
    }
  }

  def apply(year: String) {
    logger.info(s"Starting Rename for Year: $year")
    processRenameRecords(getRenameRecords(year))
  }

}

object Rename extends App {
  RenameHelpers(yearToProcess)
}