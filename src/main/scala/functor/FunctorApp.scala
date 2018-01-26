package functor

import cats.Functor
import cats.data.Nested
import cats.instances.list._
import cats.instances.option._

object FunctorApp extends App {
  val listOption = List(Some(1), None, Some(2))
  listOption.map(_.map(_ + 1)) // General way

  Functor[List].compose[Option].map(listOption)(_ + 1)

  val nested: Nested[List, Option, Int] = Nested(listOption)
  //nested.map(_ + 1)
}
