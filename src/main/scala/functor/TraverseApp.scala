package functor

import cats.data.Nested

object TraverseApp extends App {

  import cats.implicits._

  val segments: List[Either[Throwable, Int]] = List(1.asRight[Throwable])
  val result: Either[Throwable, List[Int]]   = segments.sequence[Either[Throwable, ?], Int]
  println(result)
  Nested(segments)
}
