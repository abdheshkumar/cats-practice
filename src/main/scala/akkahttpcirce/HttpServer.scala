package akkahttpcirce

import java.nio.file.{Files, Paths}

import akka.NotUsed
import akka.http.scaladsl.model.Multipart
import akka.stream.IOResult
import akka.stream.scaladsl.{Flow, Framing, Sink}
//import java.util.UUID

import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.directives.FileInfo
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Keep, Source}
import akka.util.ByteString

import scala.concurrent.Future
import scala.io.StdIn

trait FileUploader[Req, Res] extends ((FileInfo, Source[Req, Any]) => Future[Res])

object FileUploader {

  implicit def textFileUploader(implicit m: ActorMaterializer) =
    new FileUploader[ByteString, String] {
      implicit val ec = m.system.dispatcher

      override def apply(
                          v1: FileInfo,
                          v2: Source[ByteString, Any]
      ): Future[String] = {
        val path     = Files.createDirectories(Paths.get("/tmp/upload"))
        val filePath = Files.createTempFile(path, s"", s"-${v1.fileName}")
        v2.toMat(FileIO.toPath(filePath))(Keep.right)
          .run()
          .map(_ => "")
      }
    }
}

case class FileUploadRoute()(
                    implicit uploader: FileUploader[ByteString, String]
) extends Route {
  override def apply(v1: RequestContext): Future[RouteResult] = route(v1)

  private val route: Route = fileUpload("csv") {
    case (meta, byteSource) =>
      onSuccess(uploader(meta, byteSource)) { sum =>
        complete(s"Sum: $sum")
      }
  }
}

object HttpServer extends App with AkkaApp {

  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  def pSinkAnother: Sink[String, NotUsed] =
    Flow[String].to(akka.stream.scaladsl.Sink.foreach(x => println(x)))

  def printSinkAnother: Sink[String, NotUsed] =
    Flow[String]
      .map(s => s + s"Finished at ${System.nanoTime()}\n")
      .toMat(pSinkAnother)(Keep.right)

  val source = Framing.delimiter(ByteString("\n"), 1024)

  val route =
    post {
      path("upload") {
        entity(as[Multipart.FormData]) { (formData: Multipart.FormData) =>
          val extractedData: Future[Map[String, Source[ByteString, Any]]] =
            formData.parts
              .map { data =>
                data.name -> data.entity.dataBytes
              }
              .runFold(Map.empty[String, Source[ByteString, Any]])(
                (map, tuple) => map + tuple
              )

          onSuccess(extractedData.flatMap(fields => {
            println(":::" + fields.size)
            fields("name")
              .runFold(ByteString.empty)((seed, inc) => seed ++ inc)
              .flatMap(uuid => {
                fields("csv")
                  .via(Framing.delimiter(ByteString("\n"), 1024))
                  .runForeach { f =>
                    println("ss");
                    println(uuid.utf8String + f.utf8String)
                  }
              })
          }))(_ => complete("DONE"))
        }
      } ~
        path("download" / Segments) { location =>
          val source: Source[ByteString, Future[IOResult]] =
            FileIO.fromPath(Paths.get(s"/tmp/upload"))
          val re: Future[Int] = source
            .via(Framing.delimiter(ByteString("\n"), 1024))
            .mapConcat(_.utf8String.split(",").toVector)
            .map(_.toInt)
            .runFold(0) { (acc, n) =>
              acc + n
            }
          onSuccess(re)(s => complete(s"Sum: $s"))
        }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}
