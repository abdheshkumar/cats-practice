import cats.data.OptionT

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import cats.implicits._
object ScalaZApp {
  trait Terminal[C[_]] {
    def read: C[String]
    def write(t: String): C[Unit]
  }

  trait Execution[C[_]] {
    def chain[A, B](c: C[A])(f: A => C[B]): C[B]
    def create[B](b: B): C[B]
  }

  object Execution {
    implicit class Ops[A, C[_]](c: C[A]) {
      def flatMap[B](f: A => C[B])(implicit e: Execution[C]): C[B] =
        e.chain(c)(f)
      def map[B](f: A => B)(implicit e: Execution[C]): C[B] =
        e.chain(c)(f andThen e.create)
    }
  }
  import Execution._
  def echo[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    t.read.flatMap { in: String =>
      t.write(in).map { _: Unit =>
        in
      }
    }

  def echo1[C[_]](implicit t: Terminal[C], e: Execution[C]): C[String] =
    for {
      in <- t.read
      _  <- t.write(in)
    } yield in

  def getFromRedis(s: String): Future[Option[String]] = ???
  def getFromSql(s: String): Future[Option[String]]   = ???

  for {
    cache <- getFromRedis("")
    sql   <- getFromSql("")
  } yield cache orElse sql

  def getA: Future[Option[Int]] = ???
  def getB: Future[Option[Int]] = ???
  for {
    a <- OptionT(getA)
    b <- OptionT(getB)
  } yield a * b

}
