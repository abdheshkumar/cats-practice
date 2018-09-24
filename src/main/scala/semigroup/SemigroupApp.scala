package semigroup

object SemigroupApp extends App {

  import cats.Monoid
  import cats.instances.int._
  // for Monoid
  import cats.instances.option._ // for Monoid
  val a = Option(22)
  // a: Option[Int] = Some(22)
  val b = Option(20)
  // b: Option[Int] = Some(20)
  Monoid[Option[Int]].combine(a, b)
}
