package csv

import kantan.csv._
import kantan.csv.ops._
import kantan.csv.generic._
object CSVApp extends App {
  //val rawData = Source.fromFile("numbs.csv")
  val rawData: java.net.URL = getClass.getResource("nums.csv")
  val reader: CsvReader[ReadResult[List[Float]]] =
    rawData.asCsvReader[List[Float]](rfc)
  reader.foreach(f => println("sas" + f))

  /*final case class Car(year: Int, make: String, model: String, desc: Option[String], price: Float)

  val reader1: CsvReader[ReadResult[Car]] = getClass.getResource("wiki.csv").asCsvReader[Car](rfc.withHeader)*/
  //reader1.foreach(println _)
}
