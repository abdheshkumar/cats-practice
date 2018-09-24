import scala.annotation.tailrec

object Test extends App {

  /*val s = "We test coders. Give us a try?"
  val r = s.split("\\.|\\?|!").toList
    .map(f => f.split(" ").toList.filterNot(_ == "").size)
    .max
  println(r)*/
  @tailrec
  def inner(ll: List[String], r: List[String]): List[String] = {
    ll match {
      case Nil => r
      case head :: tail =>
        inner(if (tail.nonEmpty) tail.init else List.empty, r ++ (head +: tail.lastOption.toList))
    }
  }

  /*val s = 123456
  val l = s.toString.sliding(1).toList
  println(inner(l, List.empty).mkString.toInt)*/

  def isSorted(l: List[Int]): Boolean = l == l.sorted

  @tailrec
  def attemptToSort(remaining: List[Int], element: Int, r: List[Int]): Boolean = remaining match {
    case Nil => true
    case head :: Nil =>
      if (head <= element) true
      else isSorted(r :+ element)
    case head :: tail =>
      println((head +: tail) -> element)
      if (isSorted(r ++ List(element) ++ tail)) true
      else attemptToSort(tail, element, r :+ head)
  }
  @tailrec
  def in(l: List[Int], max: Int): Boolean = l match {
    case Nil => true
    case head :: tail =>
      if (head >= max) in(tail, head) else attemptToSort(head +: tail, max, List.empty)
  }

  val array = Array(1, 5, 3, 3, 7)
  val a     = Array(1, 3, 5, 3, 4)
  val aa    = Array(1, 2, 5, 3, 2)
  println(in(a.toList, a.head))
}
