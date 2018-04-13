package akka_stream_examples.reactives

import akka_stream_examples.utils.Scheduler

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt
import scala.util.Random
import scala.concurrent.ExecutionContext.Implicits.global
object Square {

  def delay: Int = Random.nextInt(5000)

  def square(a: Int): Int = a * a

  def blocking(a: Int): Int = {
    println(s"begin squaring $a")
    Thread.sleep(delay.toLong)
    val result = square(a)
    println(s"done squaring $a")
    result
  }

  def async(a: Int): Future[Int] = Future {
    blocking(a)
  }

  def nonBlocking(a: Int): Future[Int] = {
    println(s"begin squaring $a")
    Scheduler.asFuture(delay.millis) {
      val result = square(a)
      println(s"done squaring $a")
      result
    }
  }
}