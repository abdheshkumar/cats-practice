package applicative

import cats.implicits._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.control.Exception._

object ApplicativeApp extends App {
  val m: Map[Int, String] = Map(1 -> "Hi", 2 -> "There", 3 -> "you")

  m.fmap(_ ++ "!")

  val fa = Option(2)
  fa.foldLeft(Option(2))((b, n) => b.map(_ + n))

  def parseInt(s: String): Option[Int] = allCatch.opt(s.toInt)

  val l                    = List("1", "2", "3")
  val r: Option[List[Int]] = l.traverse(parseInt)

  val result: List[Option[(Int, Int)]] = (Option(1), Option(2)).traverseN { (a, b) =>
    println(s"::::${(a, b)}")
    List((a, b))
  }
  println(result)

  val result1: Option[List[(Int, Int)]] = (List(1), List(2)).traverseN { (a, b) =>
    println(s"::::${(a, b)}")
    Option((a, b))
  }
  println(result1)
  val f: (Int => Option[Int]) = Some(_)
  val rr: Option[List[Int]]   = List(1, 2, 3).traverse(f)
  println(rr)
  val r2: Option[List[Int]] = List(Option(1), Option(2)).sequence
  println(r2)

  val r3: Future[Option[Int]] = Option(12).traverse(f => Future.successful(f))
  println(r3)

  val r4: List[Either[Int, String]] =
    Either.right[List[Int], List[String]](List("1", "2")).bisequence
  println(r4)

  val r5 = (Option(1), Option("2")).bisequence
  println(r5)

}
