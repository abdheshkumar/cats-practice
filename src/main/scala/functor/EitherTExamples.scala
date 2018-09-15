package functor

object EitherTExamples extends App {
  import scala.util.Try
  import cats.implicits._

  def parseDouble(s: String): Either[String, Double] =
    Try(s.toDouble).toEither.leftMap(_ => s"$s is not a number")
}
