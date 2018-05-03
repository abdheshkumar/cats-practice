package functor

import cats.data.Nested
import cats.{Applicative, Id}
import io.circe.Encoder

object TraverseApp extends App {

  import cats.instances.list._
  import cats.syntax.traverse._
  import cats.syntax.either._
  import cats.instances.either._
  val segments: List[Either[Throwable, Int]] = List(1.asRight[Throwable])
  val result: Either[Throwable, List[Int]] = segments.sequence
  println(result)
  Nested(segments)
}
