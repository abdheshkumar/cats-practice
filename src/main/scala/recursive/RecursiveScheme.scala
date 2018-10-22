package recursive
import cats.{Functor, Show}
import cats.implicits._
import cats.kernel.Semigroup
object RecursiveScheme extends App {

  (Option(2), Option(2)).mapN((a, b) => a + b)

  case class Student(name: String, age: Int)

  case class Money(amount: Int)
  case class Salary(size: Money)

  implicit val showMoney: Show[Money]   = Show.show(m => s"$$${m.amount}")
  implicit val showSalary: Show[Salary] = showMoney.contramap(_.size)
  // Nat
  sealed trait Nat[+A]
  case class Succ[A](previous: A) extends Nat[A]
  case object Zero                extends Nat[Nothing]

  // List
  sealed trait IntList[+A]
  case class Cons[A](head: Int, tail: A) extends IntList[A]
  case object Empty                      extends IntList[Nothing]

  // Expr
  sealed trait Expr[+A]
  case class Add[A](expr1: A, expr2: A)  extends Expr[A]
  case class Mult[A](expr1: A, expr2: A) extends Expr[A]
  case class Num(literal: Int)           extends Expr[Nothing]

  // Functors
  implicit val natFunct: Functor[Nat] = new Functor[Nat] {
    def map[A, B](fa: Nat[A])(f: A => B): Nat[B] = fa match {
      case Succ(x) => Succ(f(x))
      case Zero    => Zero
    }
  }

  implicit val intListFunct: Functor[IntList] = new Functor[IntList] {
    def map[A, B](fa: IntList[A])(f: A => B): IntList[B] = fa match {
      case Cons(head, tail) => Cons(head, f(tail))
      case Empty            => Empty
    }
  }

  implicit val exprFunct: Functor[Expr] = new Functor[Expr] {
    def map[A, B](fa: Expr[A])(f: A => B): Expr[B] = fa match {
      case Add(x1, x2)  => Add(f(x1), f(x2))
      case Mult(x1, x2) => Mult(f(x1), f(x2))
      case x @ Num(_)   => x
    }
  }

  // Fixed point type
  case class Fix[F[_]](unfix: F[Fix[F]])

  // Catamorphism
  def cata[F[_]: Functor, A](structure: Fix[F])(algebra: F[A] => A): A =
    algebra(structure.unfix.map(cata(_)(algebra)))

  // Nat to Int
  def natToInt(n: Fix[Nat]): Int = cata[Nat, Int](n) {
    case Succ(x) => 1 + x
    case Zero    => 0
  }

  val nat: Fix[Nat] =
    Fix(
      Succ( // 3
        Fix(
          Succ( // 2
            Fix(
              Succ( // 1
                Fix[Nat](Zero) // 0
              )
            )
          )
        )
      )
    )
  val natRes = natToInt(nat)
  println(natRes)

  // Sum a list of ints
  def sumList(l: Fix[IntList]): Int = cata[IntList, Int](l) {
    case Cons(head, tail) => head + tail
    case Empty            => 0
  }
  val lst: Fix[IntList] =
    Fix(Cons(1, Fix(Cons(2, Fix(Cons(3, Fix[IntList](Empty)))))))
  val listRes = sumList(lst)
  println(listRes) // 6

  // Evaluate an expression
  def eval(e: Fix[Expr]): Int = cata[Expr, Int](e) {
    case Add(x1, x2)  => x1 + x2
    case Mult(x1, x2) => x1 * x2
    case Num(x)       => x
  }
  val expr: Fix[Expr] =
    Fix(
      Add(
        Fix(
          Mult(
            Fix[Expr](Num(2)),
            Fix[Expr](Num(3))
          )
        ),
        Fix[Expr](Num(3))
      )
    )
  val exprRes = eval(expr)
  println(exprRes)
}
