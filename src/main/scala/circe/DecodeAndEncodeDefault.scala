package circe

import io.circe.generic.extras.Configuration
import io.circe.generic.extras.auto._
//import io.circe.generic.auto._
import io.circe._, io.circe.parser._
import io.circe.syntax._


object DecodeAndEncodeDefault extends App {


  implicit val withDefaultsConfig: Configuration = Configuration.default.withDefaults

  val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)

  case class Test(key: Option[String])

  val rawJson = """{"key": null}"""

  val json: Json = parse(rawJson).getOrElse(Json.Null)

  println(json.as[Test])
  println(Test(key = None).asJson.pretty(printer))
}
