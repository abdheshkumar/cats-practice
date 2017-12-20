package parsing

import io.circe._
import io.circe.parser._
import io.circe.generic.semiauto._


import scala.io.Source

object CircleApp extends App {

  sealed trait Cell
  case class Edge(`type`: String, source: Port, target: Port) extends Cell
  case class Node(id: String, `type`: String, nodeMetadata: Option[NodeMetadata]) extends Cell

  implicit val decodePort: Decoder[Port] = deriveDecoder[Port]
  implicit val decodeNodeMetadata: Decoder[NodeMetadata] = deriveDecoder[NodeMetadata]
  implicit val decodeEdge: Decoder[Edge] = deriveDecoder[Edge]
  implicit val decodeNode: Decoder[Node] = deriveDecoder[Node]
  implicit val decodeCells: Decoder[Cells] = deriveDecoder[Cells]

  implicit val decodeCell: Decoder[Cell] = Decoder.instance(c =>
    c.downField("type").as[String].flatMap {
      case "link" => c.as[Edge]
      case _ => c.as[Node]
    }
  )

  case class Port(id: String, port: String)

  case class NodeMetadata(byteString: String)


  case class Cells(cells: Seq[Cell])

  val jsonString = Source.fromResource("file.json").mkString
  parse(jsonString) match {
    case Left(ex) => println(ex)
    case Right(json) =>
      val cells = (json.as[Cells])
      println(cells.map(_.cells.length))
  }

}


