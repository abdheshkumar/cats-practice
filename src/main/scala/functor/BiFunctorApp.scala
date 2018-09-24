package functor

object BiFunctorApp extends App {

  import BifunctorW._

  val x: Either[Int, String] = Left(7)
  val y                      = ("hello", 42)

  val f = (n: Int) => n + 1
  val g = (s: String) => s.reverse
  val h = (s: String) => s.toUpperCase
  val i = (n: Int) => n * 2

  x.<-:->(_.toString, _.toInt)
  val p = f <-: x :-> g
  val q = h <-: y :-> i

}

trait Bifunctor[F[+ _, + _]] {
  def bimap[A, B, C, D](fa: F[A, B], f: A => C, g: B => D): F[C, D]
}

object Bifunctor {
  implicit def Tuple2Bifunctor: Bifunctor[Tuple2] = new Bifunctor[Tuple2] {
    def bimap[A, B, C, D](fa: (A, B), f: A => C, g: B => D): (C, D) =
      (f(fa._1), g(fa._2))
  }

  implicit def EitherBifunctor: Bifunctor[Either] = new Bifunctor[Either] {
    def bimap[A, B, C, D](fa: Either[A, B], f: A => C, g: B => D): Either[C, D] =
      fa match {
        case Left(a)  => Left(f(a))
        case Right(b) => Right(g(b))
      }
  }
}

trait BifunctorW[F[+ _, + _], A, B] {
  val value: F[A, B]
  val bifunctor: Bifunctor[F]

  def <-:->[C, D](f: A => C, g: B => D): F[C, D] = bifunctor.bimap(value, f, g)

  def <-:[C](f: A => C): F[C, B] = bifunctor.bimap(value, f, identity[B])

  def :->[D](g: B => D): F[A, D] = bifunctor.bimap(value, identity[A], g)
}

object BifunctorW {

  def bifunctor[F[+ _, + _]]: BifunctorApply[F] = new BifunctorApply[F] {
    def apply[A, B](v: F[A, B])(implicit b: Bifunctor[F]): BifunctorW[F, A, B] =
      new BifunctorW[F, A, B] {
        val value     = v
        val bifunctor = b
      }
  }

  trait BifunctorApply[F[+ _, + _]] {
    def apply[A, B](v: F[A, B])(implicit b: Bifunctor[F]): BifunctorW[F, A, B]
  }

  implicit def Tuple2Bifunctor[A, B](v: (A, B)): BifunctorW[Tuple2, A, B] =
    bifunctor[Tuple2](v)

  implicit def Either2Bifunctor[A, B](
                      v: Either[A, B]
  ): BifunctorW[Either, A, B] = bifunctor[Either](v)
}
