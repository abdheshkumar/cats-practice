object PairApp extends App {

  trait Monoid[A] {
    def empty: A

    def combine(x: A, y: A): A
  }

  // Implementation for Int
  implicit val intAdditionMonoid: Monoid[Int] = new Monoid[Int] {
    def empty: Int = 0

    def combine(x: Int, y: Int): Int = x + y
  }

  implicit val stringMonoid: Monoid[String] = new Monoid[String] {
    def empty: String = ""

    def combine(x: String, y: String): String = x ++ y
  }

  object Demo {

    final case class Pair[A, B](first: A, second: B)

    object Pair {
      implicit def tuple2Instance[A, B](
                          implicit A: Monoid[A],
                          B: Monoid[B]
      ): Monoid[Pair[A, B]] =
        new Monoid[Pair[A, B]] {
          def empty: Pair[A, B] = Pair(A.empty, B.empty)

          def combine(x: Pair[A, B], y: Pair[A, B]): Pair[A, B] =
            Pair(A.combine(x.first, y.first), B.combine(x.second, y.second))
        }
    }

  }

  def combineAll[A](list: List[A])(implicit A: Monoid[A]): A =
    list.foldRight(A.empty)(A.combine)

  import Demo.{Pair => Paired}

  combineAll(List(Paired(1, "hello"), Paired(2, " "), Paired(3, "world")))
}
