
/**
  * Created by abdhesh on 29/04/17.
  */
object TypeClasses extends App {

  trait Combiner[A] {
    def combine(a: A, b: A): A

    def zero: A
  }

  def genericSum[A](as: List[A], c: Combiner[A]): A =
    as.foldRight(c.zero)(c.combine)

  val intCombiner = new Combiner[Int] {
    def combine(a: Int, b: Int): Int = a + b

    def zero: Int = 0
  }

  genericSum(List(1, 2, 3), intCombiner)

  def genericSum2[A](as: List[A])(implicit c: Combiner[A]): A =
    as.foldRight(c.zero)(c.combine)

  implicit val IntCombiner = intCombiner

  implicit val BooleanCombiner = new Combiner[Boolean] {
    def combine(a: Boolean, b: Boolean): Boolean = a && b

    def zero: Boolean = true
  }

  genericSum2(List(1, 2, 3))

  genericSum2(List(true, false, true))

  //genericSum2(List("foo", "bar", "baz"))

  implicit class CombinerSyntax[A](as: List[A])(implicit c: Combiner[A]) {
    def gsum: A = genericSum2(as) // c will be passed along because it's implicit here
  }

  implicit class CombinerSyntax2[A: Combiner](as: List[A]) {
    def gsum2: A = genericSum2(as) // unnamed Combiner[A] is implicit here
  }

  List(1, 2).gsum

  implicit def ListCombiner[A] = new Combiner[List[A]] {
    def combine(a: List[A], b: List[A]): List[A] = a ::: b

    def zero: List[A] = List.empty[A]
  }

  List(List('a', 'b'), List('c', 'd')).gsum2

  implicit class ASyntax[A](a: A)(implicit c: Combiner[A]) {
    def |+|(b: A): A = c.combine(a, b)
  }

  1 |+| 2
  true |+| true |+| false

  implicit def PairCombiner[A, B](implicit ca: Combiner[A], cb: Combiner[B]): Combiner[(A, B)] =
    new Combiner[(A, B)] {
      def combine(a: (A, B), b: (A, B)): (A, B) = (a._1 |+| b._1, a._2 |+| b._2)

      def zero: (A, B) = (ca.zero, cb.zero)
    }

  ((1 -> true) |+| (2 -> true)) |+| (3 -> true)

  (List('a', 'b') -> 5) |+| (List('d', 'e') -> 10)

  List((1, 2), (3, 4)).gsum

  val a = (1, ((true, 7), List('a', 'b')))
  val b = (9, ((true, 8), List('c', 'd')))
  a |+| b
}
