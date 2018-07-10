package monoid

import cats._
import cats.implicits._

object MonoidApp extends App {

  case class Cat(
                      name: String,
                      yearOfBirth: Int,
                      favoriteFoods: List[String]
  )

  val tupleToCat: (String, Int, List[String]) => Cat =
    Cat.apply _
  val catToTuple: Cat => (String, Int, List[String]) =
    cat => (cat.name, cat.yearOfBirth, cat.favoriteFoods)
  implicit val catMonoid: Monoid[Cat] = (
    Monoid[String],
    Monoid[Int],
    Monoid[List[String]]
  ).imapN(tupleToCat)(catToTuple)

}
