import cats.{Semigroup, Show}
import cats.implicits._
import java.util.Date
case class Money(amount: Int)

case class Salary(size: Money)

implicit val showMoney: Show[Money] = Show.show(m => s"$$${m.amount}")
implicit val showSalary: Show[Salary] = showMoney.contramap(_.size)

Salary(Money(1000)).show


def longToDate: Long => Date = new Date(_)
def dateToLong: Date => Long = _.getTime

implicit val semigroupDate: Semigroup[Date] =
  Semigroup[Long].imap(longToDate)(dateToLong)

val today: Date = longToDate(1449088684104l)
val timeLeft: Date = longToDate(1900918893l)

today |+| timeLeft