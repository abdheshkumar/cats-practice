package circe

import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec, JsonKey}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.literal._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}

object CirceJsonApp extends App {

  sealed trait ConfigExampleBase

  case class ConfigExampleFoo(thisIsAField: String, a: Int = 0, b: Double) extends ConfigExampleBase

  case object ConfigExampleBar extends ConfigExampleBase

  //JSON Literal
  implicit val customConfig: Configuration =
    Configuration.default.withSnakeCaseMemberNames.withDefaults.withDiscriminator("type")

  //val foo: ConfigExampleBase = ConfigExampleFoo(f, 0, b)
  val json =
    json"""{"testKey": "sa", "b": 2}"""
  val expected = json"""{ "type": "ConfigExampleFoo", "this_is_a_field": "sa", "a": 0, "b": 2}"""
  val s        = json"{}"
  println(json)
  implicit val config: Configuration = Configuration.default
  println(json.hcursor.downField("testKey").as[String])

  sealed trait Foo
  final case object Bar         extends Foo
  final case object Boo         extends Foo
  case class TestFoo(t: String) extends Foo
  implicit val decodeConfigExampleBase: Decoder[Foo] = deriveDecoder
  implicit val encodeConfigExampleBase: Encoder[Foo] = deriveEncoder
  val boo: Foo                                       = TestFoo("Test")
  println(boo.asJson.noSpaces) //{"type":"Boo"}

}
