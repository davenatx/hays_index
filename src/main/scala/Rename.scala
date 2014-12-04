package com.austindata

import DBHelpers._

import java.sql.Date

object RenameHelpers extends App {
  // Example retrieveing RenameRecords
  val records = OPRRecordsByYear("1920");
  println(records)
  println("Number of Records: " + records.size)
}