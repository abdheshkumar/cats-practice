package case_study

import cats.Monoid
import cats.implicits._
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global

object CaseStudy2 extends App {

  def foldMap[A, B: Monoid](fa: Vector[A])(f: A => B): B =
    fa.foldLeft(Monoid[B].empty)((b, a) => Monoid[B].combine(b, f(a)))

  def parallelFoldMap[A, B: Monoid](values: Vector[A])(func: A => B): Future[B] = {
    val processors = Runtime.getRuntime.availableProcessors()
    val groupSize  = (1.0 * values.size / processors).ceil.toInt
    val futures = values
      .grouped(groupSize)
      .map { group =>
        Future {
          foldMap(group)(func)
        }
      }
    Future.sequence(futures) map { iterable =>
      iterable.foldLeft(Monoid[B].empty)(_ |+| _)
    }
  }

  def parallelFoldMapCat[A, B: Monoid](values: Vector[A])(func: A => B): Future[B] = {
    val numCores  = Runtime.getRuntime.availableProcessors
    val groupSize = (1.0 * values.size / numCores).ceil.toInt
    values
      .grouped(groupSize)
      .toVector
      .traverse(group => Future(group.toVector.foldMap(func)))
      .map(_.combineAll)
  }

  println(foldMap(Vector(1, 2, 3))(identity))
  val result: Future[Int] = parallelFoldMap((1 to 1000000).toVector)(identity)
  val r                   = Await.result(result, 1.second)
  println(r)
}
