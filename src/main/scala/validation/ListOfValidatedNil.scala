package validation

object ListOfValidatedNil extends App {
  // Start writing your ScalaFiddle code here
  import cats.implicits._

  case class User(f: String, l: String, e: String)

  val validations1 = List(1.validNel[String], 2.valid, 3.valid)
  val validations2 =
    List(1.validNel[String], "kaboom".invalidNel, "boom".invalidNel)

  val valSum1 = validations1.combineAll
  // Valid(6)
  val valSum2 = validations2.combineAll

}
