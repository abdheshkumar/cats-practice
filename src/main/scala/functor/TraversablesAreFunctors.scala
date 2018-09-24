package functor

import cats.{Applicative, Functor}

object TraversablesAreFunctors extends App {
  final case class Id[A](value: A)
  implicit val applicativeForId: Applicative[Id] = new Applicative[Id] {
    def ap[A, B](ff: Id[A => B])(fa: Id[A]): Id[B] = Id(ff.value(fa.value))

    def pure[A](a: A): Id[A] = Id(a)
  }

  trait Traverse[F[_]] extends Functor[F] {

    def traverse[G[_]: Applicative, A, B](fa: F[A])(f: A => G[B]): G[F[B]]

    def map[A, B](fa: F[A])(f: A => B): F[B] =
      traverse(fa)(a => Id(f(a))).value
  }
}
