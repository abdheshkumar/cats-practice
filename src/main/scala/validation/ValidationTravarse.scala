package validation

import cats.implicits._
import cats.data._

object ValidationTravarse extends App {

  case class Error()

  case class User(firstName: String)

  def validate(u: String): ValidatedNel[Error, User] =
    User(u).validNel

  val lines: List[String] = List("user1")
  val users: ValidatedNel[Error, List[User]] =
    lines.traverse(validate)
}
