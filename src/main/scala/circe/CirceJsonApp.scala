package circe

import cats.kernel.Eq
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder, Json, ObjectEncoder}

import io.circe.literal._
import io.circe.syntax._
import shapeless.Witness
import shapeless.labelled.{FieldType, field}


object CirceJsonApp extends App {

  /*sealed trait ConfigExampleBase

  case class ConfigExampleFoo(thisIsAField: String, a: Int = 0, b: Double) extends ConfigExampleBase

  case object ConfigExampleBar extends ConfigExampleBase

  //JSON Literal
  implicit val customConfig: Configuration =
    Configuration.default.withSnakeCaseMemberNames.withDefaults.withDiscriminator("type")
*/
  /* //val foo: ConfigExampleBase = ConfigExampleFoo(f, 0, b)
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
   println(json.hcursor.downField("testKey").as[String])*/

  /* implicit val decodeIntlessQux: Decoder[Int => Qux[String]] =
     deriveFor[Int => Qux[String]].incomplete

   implicit val decodeJlessQux: Decoder[FieldType[Witness.`'j`.T, Int] => Qux[String]] =
     deriveFor[FieldType[Witness.`'j`.T, Int] => Qux[String]].incomplete

   implicit val decodeQuxPatch: Decoder[Qux[String] => Qux[String]] = deriveFor[Qux[String]].patch
 */
  /* sealed trait Foo
   final case object Bar extends Foo
   final case object Boo extends Foo
   case class TestFoo(t: String) extends Foo
   implicit val decodeConfigExampleBase: Decoder[Foo] = deriveDecoder
   implicit val encodeConfigExampleBase: Encoder[Foo] = deriveEncoder
   val boo: Foo = TestFoo("Test")
   println(boo.asJson.noSpaces) //{"type":"Boo"}*/


}

