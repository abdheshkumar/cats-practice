import cats.data.Nested
import cats.{Applicative, Functor, Traverse}
import cats.implicits._

import scala.util.control.Exception.allCatch

val alo = Applicative[Option]
  .compose[List]
  .compose[Option]
  .compose[List]

val p = alo.pure(12)

alo.map(Some(List(None: Option[List[Int]])))(f => f + 2)

val m: Map[Int, String] = Map(1 -> "Hi", 2 -> "There", 3 -> "you")

m.fmap(_ ++ "!")

val fa = Option(2)
fa.foldLeft(Option(2))((b, n) => b.map(_ + n))

def parseInt(s: String): Option[Int] = allCatch.opt(s.toInt)

val l = List("1", "2", "3")
val r: Option[List[Int]] = l.traverse(parseInt)
List("1", "abc").traverse(parseInt)

val l2: List[Option[List[Int]]] = List(Some(List(1, 2)), Some(List(3, 4)))
l2.flatSequence

val l3: List[Option[List[Int]]] = List(Some(List(1, 2)), None)
l3.flatSequence

val l4: Option[List[String]] = Option(List("1", "2"))
l4.flatTraverse(_.map(parseInt))

(Option(1), Option(2)).traverseN {
  (a, b) =>
    println("::::" + (a, b))
    Option(a, b)
}

val alo1 = Traverse[Option]
  .compose[List]
  .compose[Option]
  .compose[List]
alo1.traverse(Option(List(Option(List(1)))))(f => List(f))

val listOpt = List(Some(12), None)
val lf: Int => String = i => (i * 2).toString()
Functor[List].map(listOpt)(opt => opt.map(lf))

val nested: Nested[List, Option, Int] = Nested(listOpt)

Functor[Nested[List, Option, ?]].map(nested)(lf).value


val listOption = List(Some(1), None, Some(2))
val nested1: Nested[List, Option, Int] = Nested(listOption)
nested1.map(lf).value