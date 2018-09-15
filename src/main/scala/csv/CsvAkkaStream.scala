package csv

import akka.NotUsed
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Flow, Keep, Sink, Source}

import scala.concurrent.duration._
import scala.concurrent.Await

object CsvAkkaStream extends App {
  implicit val system = ActorSystem("src/main/csv/main/csv")
  implicit val ac     = ActorMaterializer()
  val flow: Flow[Seq[String], Map[String, String], NotUsed] = Flow[Seq[String]]
    .prefixAndTail(1)
    .flatMapConcat {
      case (headers, rows) =>
        rows.map { row =>
          println(headers)
          headers.head.zip(row).toMap
        }
    }

  val test: Source[Seq[String], NotUsed] = Source(
    List(Seq("col1", "col2"), Seq("a", "b"), Seq("1", "2"))
  )
  test
    .take(2)
    .toMat(Sink.seq)(Keep.right)
    .run()

  Await.result(test.via(flow).runForeach(println), 20.seconds)
}
