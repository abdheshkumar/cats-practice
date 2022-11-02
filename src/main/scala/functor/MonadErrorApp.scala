package functor

import cats.MonadError
import cats.implicits._

object MonadErrorApp extends App {

  def validateName[F[_]](a: String)(implicit M: MonadError[F, Throwable]): F[String] =
    if (a.length > 6) M.pure(a)
    else M.raiseError(new Exception("Name is not valid"))

  def validateAge[F[_]](a: Int)(implicit M: MonadError[F, Throwable]): F[Int] =
    if (a > 20) M.pure(a)
    else M.raiseError(new Exception("Name is not valid"))

  val result: Either[Throwable, (String, Int)] = for {
    name <- validateName[Either[Throwable, *]]("Test hello")
    age  <- validateAge[Either[Throwable, *]](25)
  } yield (name, age)

  val resultEither = for {
    name <- validateName[Either[Throwable, *]]("Test hello").attemptT
    age  <- validateAge[Either[Throwable, *]](25).attemptT
  } yield (name, age)

  println(resultEither.value)
}
