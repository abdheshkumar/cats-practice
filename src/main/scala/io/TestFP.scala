/*
package io

import cats.effect.{IO, Sync}

object TestFP extends App {

  import cats._
  import cats.data._
  import cats.implicits._
  //import cats.data.EitherT._

  trait A1[F[_]] {
    def test(): F[String]

    def error(): F[Unit]
  }

  object A1 {
    def apply[F[_]](implicit a1: A1[F]): A1[F] = a1

    implicit def a1[F[_]](implicit ae: ApplicativeError[F, String]): A1[F] = new A1[F] {
      override def test(): F[String] = ae.pure("test")

      override def error(): F[Unit] = ae.raiseError("error a1")
    }
  }

  trait A2[F[_]] {
    def error(): F[Unit]
  }

  object A2 {

    sealed trait DomainError

    final case class NotFoundError(id: Int) extends DomainError

    final case class DependenciesError(msg: String) extends DomainError

    def apply[F[_]](implicit a2: A2[F]): A2[F] = a2

    implicit def a2[F[_]](implicit ae: ApplicativeError[F, DomainError]): A2[F] =
      () => ae.raiseError(NotFoundError(1))
  }

  import A1._
  import A2._

  type Effect[A] = EitherT[IO, A2.DomainError, A]

  def program[F[_] : Monad](implicit a1: A1[F]): F[Unit] = for {
    testA1 <- a1.test()
    //testA2 <- a2.error().attempt
  } yield ()

  println(program[Effect].value.unsafeRunSync())
}*/
