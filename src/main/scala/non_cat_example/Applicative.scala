package non_cat_example

trait Applicative[F[_]] extends Functor[F] {
  self =>
  def pure[A](a: A): F[A]

  def ap[A, B](fa: F[A])(ff: F[A => B]): F[B]

  def product[A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    tuple2(fa, fb)

  def tuple2[A, B](fa: F[A], fb: F[B]): F[(A, B)] =
    map2(fa, fb)((a, b) => (a, b))

  def tuple3[A, B, C](fa: F[A], fb: F[B], fc: F[C]): F[(A, B, C)] =
    map3(fa, fb, fc)((a, b, c) => (a, b, c))

  def map[A, B](fa: F[A])(f: A => B): F[B] =
    ap(fa)(pure(f))

  def map2[A, B, Z](fa: F[A], fb: F[B])(f: (A, B) => Z): F[Z] =
    ap(fa)(map(fb)(b => f(_, b)))

  def map3[A, B, C, Z](fa: F[A], fb: F[B], fc: F[C])(f: (A, B, C) => Z): F[Z] =
    ap(fa)(map2(fb, fc)((b, c) => f(_, b, c)))

  //Use divide and conquer approach
  def map4[A, B, C, D, Z](fa: F[A], fb: F[B], fc: F[C], fd: F[D])(f: (A, B, C, D) => Z): F[Z] = {
    val t1 = tuple2(fa, fb)
    val t2 = tuple2(fc, fd)
    map2(t1, t2) { case ((a, b), (c, d)) => f(a, b, c, d) }
  }

  def flip[A, B](fab: F[A => B]): F[A] => F[B] =
    fa => ap(fa)(fab)

  def compose[G[_]](implicit G: Applicative[G]): Applicative[Lambda[X => F[G[X]]]] = new Applicative[Lambda[X => F[G[X]]]] {
    def pure[A](a: A): F[G[A]] = self.pure(G.pure(a))

    def ap[A, B](fga: F[G[A]])(ff: F[G[A => B]]): F[G[B]] = {
      val x: F[G[A] => G[B]] = self.map(ff)(g => G.flip(g))
      self.ap(fga)(x)
    }


  }
}

object Applicative {
  def apply[F[_]](implicit applicative: Applicative[F]): Applicative[F] = applicative
}
