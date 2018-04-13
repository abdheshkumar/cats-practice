package akka_stream_examples.reactives

import akka.NotUsed
import akka.stream.scaladsl.Source
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Squares {

  def blocking(numbers: Seq[Int]): Seq[Int] = {
    val squares = numbers.map(n => Square.blocking(n))

    squares
  }

  def async(numbers: Seq[Int]): Future[Seq[Int]] = {
    val futures = numbers.map(n => Square.async(n))

    Future.sequence(futures)
  }

  def nonBlocking(numbers: Seq[Int]): Future[Seq[Int]] = {
    val futures = numbers.map(n => Square.nonBlocking(n))

    Future.sequence(futures)
  }

  def streaming(numbers: Seq[Int]): Source[Int, NotUsed] = {
    val stream = Source(numbers.toList)
    stream.mapAsync(10)(Square.nonBlocking)
  }
}