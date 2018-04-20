package validation

import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import cats.data.Validated._

object ValidatedResult extends App {
  type ValidationResult[A] = Validated[NonEmptyList[String], A]

  def a: Validated[List[String], String] = Valid("1")

  def b: Validated[List[String], Int] = Invalid(List("Invalid-b"))

  def c: Validated[List[String], Int] = Invalid(List("Invalid-c"))

  def d: Validated[List[String], Int] = Invalid(List("Invalid-d"))

  val result = (a, b, c, d).mapN((aa, bb, cc, dd) => (aa, bb, cc, dd))
  println(result)

}
