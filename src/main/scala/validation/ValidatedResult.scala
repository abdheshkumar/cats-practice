package validation

import cats.data.{NonEmptyList, Validated}
import cats.implicits._
import cats.data.Validated._

object ValidatedResult extends App {
  type ValidationResult[A] = Validated[List[String], A]

  def a: ValidationResult[String] = Valid("1")

  def b: ValidationResult[Int] = Invalid(List("Invalid-b"))

  def c: ValidationResult[Int] = Invalid(List("Invalid-c"))

  def d: ValidationResult[Int] = Invalid(List("Invalid-d"))

  val result = (a, b, c, d).mapN((aa, bb, cc, dd) => (aa, bb, cc, dd))
  println(result)

}
