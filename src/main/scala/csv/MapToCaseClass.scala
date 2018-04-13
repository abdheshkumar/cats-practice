package csv

object MapToCaseClass extends App {

  import Conversions._

  case class Foo(a: String, b: Int, c: Boolean)

  def m: Map[String, String] = Map("a" -> "hello", "c" -> "true", "b" -> "100")

  def e: Map[String, String] = Map("c" -> "true", "b" -> "a100")

  val schema = Schema.of[Foo]

  val result = schema.readFrom(m)
  val error = schema.readFrom(e)
  println(result)
  println(error)
}
