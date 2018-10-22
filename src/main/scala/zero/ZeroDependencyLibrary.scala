package zero
import scala.util.{Failure, Success, Try}

import scala.concurrent.{ExecutionContext, Future}
object ZeroDependencyLibrary {

  trait Async[M[_]] {
    def liftM[A](value: A): M[A]
    def exception[A](ex: Throwable): M[A]
    def map[A, B](fa: M[A], f: A => B): M[B]
    def flatMap[A, B](fa: M[A], f: A => M[B]): M[B]
    def flattenM[A](fa: Seq[M[A]]): M[Seq[A]]
    def sideEffect[A](fa: M[A], f: Try[A] => Unit): M[A]
  }

  object Async {
    def apply[M[_]](implicit async: Async[M]): Async[M] = async
    def liftTry[M[_], A](tryA: Try[A])(implicit async: Async[M]): M[A] = tryA match {
      case Success(value)     => async.liftM(value)
      case Failure(exception) => async.exception(exception)
    }
  }

  final class AsyncOps[M[_], A](val ma: M[A]) extends AnyVal {
    def map[B](f: A => B)(implicit asyncM: Async[M]): M[B]        = asyncM.map(ma, f)
    def flatMap[B](f: A => M[B])(implicit asyncM: Async[M]): M[B] = asyncM.flatMap(ma, f)
  }

  trait AsyncSyntax {
    implicit def toAsyncOps[M[_], A](ma: M[A]): AsyncOps[M, A] = new AsyncOps[M, A](ma)
  }

  object async extends AsyncSyntax

  trait FutureInstance {
    implicit def asyncFutureInstance(implicit ec: ExecutionContext): Async[Future] =
      new Async[Future] {
        override def liftM[A](value: A): Future[A]                              = Future.successful(value)
        override def exception[A](ex: Throwable): Future[A]                     = Future.failed(ex)
        override def map[A, B](fa: Future[A], f: A => B): Future[B]             = fa.map(f)
        override def flatMap[A, B](fa: Future[A], f: A => Future[B]): Future[B] = fa.flatMap(f)
        override def flattenM[A](fa: Seq[Future[A]]): Future[Seq[A]]            = Future.sequence(fa)
        override def sideEffect[A](fa: Future[A], f: Try[A] => Unit): Future[A] = fa.transform {
          tf =>
            f(tf)
            tf
        }
      }
  }
  object future extends FutureInstance

  trait My[A] {
    def format(value: A): String
    def contramap[B](fun: B => A): My[B]
  }

  trait Monoid[A] {
    def append(a: A, b: A): A
    def identity: A
    /*
   * Such that:
   * Associativity property: `append(a, append(b,c)) == append(append(a,b),c)`
   * Identity property: `append(a, identity) == append(identity, a) == a`
   */
  }

  object FunctionComposition /* extends Monoid[_ => _] */ {
    def append[A, B, C](a: A => B, b: B => C): A => C = a.andThen(b)
    def identity[A]: A => A                           = a => a
    // Associativity: (f.andThen(g.andThen(h)))(x) == ((f.andThen(g)).andThen(h))(x)
    // Identity: identitity(f(x)) == f(identity(x)) == f(x)
  }

  trait Functor[F[_]] {
    def map[A, B](a: F[A])(fn: A => B): F[B]
    // Identity: map(fa)(identity) == fa
    // Composition: map(fa)(f andThen g) == map(map(fa)(f))(g)
  }

  trait Monad[M[_]] {
    def pure[A](a: A): M[A]
    def flatMap[A, B](a: M[A])(fn: A => M[B]): M[B]
  }

  trait Monad1[M[_]] extends Functor[M] {
    def pure[A](a: A): M[A]
    def flatMap[A, B](a: M[A])(fn: A => M[B]): M[B]
    def map[A, B](a: M[A])(fn: A => B): M[B] =
      flatMap(a) { b: A =>
        pure(fn(b))
      }
  }

//https://engineering.sharethrough.com/blog/2016/04/18/explaining-monads-part-1/
  /*
  Monads are useful because they allow you to compose functions for values in a context (M[_])
   */
  trait Monad2[M[_]] { // extends Monoid[_ => M[_]]
    def pure[A](a: A): M[A]
    def flatMap[A, B](a: M[A])(fn: A => M[B]): M[B]

    def map[A, B](a: M[A])(fn: A => B): M[B] =
      flatMap(a) { b: A =>
        pure(fn(b))
      }

    def append[A, B, C](f1: A => M[B], f2: B => M[C]): A => M[C] = { a: A =>
      val bs: M[B] = f1(a)
      val cs: M[C] = flatMap(bs) { b: B =>
        f2(b)
      }
      cs
    }

    def identity[A]: A => M[A] = a => pure(a)

    // And the laws apply!
    // Associativity: flatMap(pure(a), x => flatMap(f(x), g)) == flatMap(flatMap(pure(a), f), g)
    // Identity: flatMap(pure(a), f) == flatMap(f(x), pure) == f(x)
  }

}
