package csv

import akka.stream.scaladsl.{Flow, Source}
import akkahttpcirce.AkkaApp
import cats.Functor
import cats.syntax.functor._

import scala.language.higherKinds

object AkkaStreamFunctor extends AkkaApp with App {

  def flow[F[_]: Functor] =
    Flow[Message[Int]].map(msg => msg.map(x => x.toString))

  def flow1(implicit F: Functor[Message]) =
    Flow[Message[Int]].map(msg => msg.map(x => x.toString))

  Source(List(1, 2, 3))
    .map(f => Message(f))
    .via(flow)
    .runForeach(println)
}

case class Message[T](value: T)

object Message {
  implicit val messageFunctor: Functor[Message] = new Functor[Message] {
    override def map[A, B](fa: Message[A])(f: A => B): Message[B] =
      Message(f(fa.value))
  }

}
