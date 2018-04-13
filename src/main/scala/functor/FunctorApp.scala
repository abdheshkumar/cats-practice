package functor

import java.util.Date

import cats.data.Nested
import cats.implicits._
import cats.{Functor, Semigroup, Show}

object FunctorApp extends App {
  val listOption = List(Some(1), None, Some(2))
  listOption.map(_.map(_ + 1)) // General way

  Functor[List].compose[Option].map(listOption)(_ + 1)

  val nested: Nested[List, Option, Int] = Nested(listOption)
  //nested.map(_ + 1)

  val result: Either[String, String] = "Hello".asRight[String]
  val out: Either[String, String] = result.bimap(_.toLowerCase, _.toUpperCase())


  case class Money(amount: Int)

  case class Salary(size: Money)

  implicit val showMoney: Show[Money] = Show.show(m => s"$$${m.amount}")
  implicit val showSalary: Show[Salary] = showMoney.contramap(_.size)

  Salary(Money(1000)).show


  def longToDate: Long => Date = new Date(_)

  def dateToLong: Date => Long = _.getTime

  implicit val semigroupDate: Semigroup[Date] = Semigroup[Long].imap(longToDate)(dateToLong)

  val today: Date = longToDate(1449088684104l)
  val timeLeft: Date = longToDate(1900918893l)

  today |+| timeLeft

  def by[T, S](f: T => S)(implicit ord: Ordering[S]): Ordering[T] = new Ordering[T] {
    override def compare(x: T, y: T): Int = ord.compare(f(x), f(y))
  }

  import scala.math.Ordered._

  implicit val moneyOrdering: Ordering[Money] = Ordering.by(_.amount)

  Money(100) < Money(200)
}
