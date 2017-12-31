package semigroup

import cats.Semigroup

object MergingMaps extends App {

  import cats.instances.all._
  import cats.syntax.semigroup._

  def optionCombine[A: Semigroup](a: A, opt: Option[A]): A =
    opt.map(a |+| _).getOrElse(a)

  def mergeMap[K, V: Semigroup](lhs: Map[K, V], rhs: Map[K, V]): Map[K, V] =
    lhs.foldLeft(rhs) {
      case (acc, (k, v)) => acc.updated(k, optionCombine(v, acc.get(k)))
    }

  val xm1 = Map('a' -> 1, 'b' -> 2)
  val xm2 = Map('b' -> 3, 'c' -> 4)
  val x = mergeMap(xm1, xm2)
  println(x)
  val ym1 = Map(1 -> List("hello"))
  val ym2 = Map(2 -> List("cats"), 1 -> List("world"))
  val y = mergeMap(ym1, ym2)
  println(y)
  val z = Semigroup[Map[Char, Int]].combine(xm1, xm2)
  println(z)
  val v = Semigroup[Map[Int, List[String]]].combine(ym1, ym2)
  println(v)
}
