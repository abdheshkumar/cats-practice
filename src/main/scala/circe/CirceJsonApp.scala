package circe

import io.circe.Encoder
import io.circe.generic.extras.{Configuration, ConfiguredJsonCodec, JsonKey}
import io.circe.literal._
import io.circe.syntax._
import io.circe.generic.auto._

object CirceJsonApp extends App {
  //JSON Literal
  /* implicit val customConfig: Configuration =
    Configuration.default.withSnakeCaseMemberNames.withDefaults.withDiscriminator("type")*/

  //val foo: ConfigExampleBase = ConfigExampleFoo(f, 0, b)
  val json =
    json"""{"testKey": "sa", "b": 2}"""
  val expected = json"""{ "type": "ConfigExampleFoo", "this_is_a_field": "sa", "a": 0, "b": 2}"""
  val s        = json"{}"
  println(json)
  implicit val config: Configuration = Configuration.default

  @ConfiguredJsonCodec case class Bar(@JsonKey("my-int") i: Int, s: String)

  implicit val encodeBar: Encoder[Bar] =
    Encoder.forProduct2("my-int", "s")(b => (b.i, b.s))
  println(Bar(13, "Qux").asJson)
  println(json.hcursor.downField("testKey").as[String])
}
