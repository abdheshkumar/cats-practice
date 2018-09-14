package parsing

//import io.circe.Decoder
import io.circe.Json
import io.circe.generic.extras.Configuration
import io.circe.parser._
import io.circe.generic.extras.auto._

object DefaultParser extends App {

  case class ConfigExampleFoo(thisIsAField: String, a: Int = 0, b: Double)

  implicit val withDefaultsConfig: Configuration = Configuration.default.withDefaults

  val rawJson    = """{ "thisIsAField": "hehe", "b": 2.3 }"""
  val json: Json = parse(rawJson).right.get
  println(decode[ConfigExampleFoo](rawJson))

}
