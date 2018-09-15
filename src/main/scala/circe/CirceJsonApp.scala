package circe
import io.circe.literal._

object CirceJsonApp extends App {
//JSON Literal
  /* implicit val customConfig: Configuration =
    Configuration.default.withSnakeCaseMemberNames.withDefaults.withDiscriminator("type")*/

  //val foo: ConfigExampleBase = ConfigExampleFoo(f, 0, b)
  val json     = json"""{ "type": "ConfigExampleFoo", "this_is_a_field": "sa", "b": 2}"""
  val expected = json"""{ "type": "ConfigExampleFoo", "this_is_a_field": "sa", "a": 0, "b": 2}"""
  val s        = json"{}"
  println(json)
}
