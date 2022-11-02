package functor

object TraversablesAreFoldable {
  import cats.{Applicative, Monoid, Traverse}
  import cats.data.Const

  def foldMap[F[_]: Traverse, A, B: Monoid](fa: F[A])(f: A => B): B =
    Traverse[F].traverse[Const[B, *], A, B](fa)(a => Const(f(a))).getConst
}
