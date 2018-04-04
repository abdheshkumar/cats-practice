package akkahttpcirce

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{RequestContext, Route, RouteResult}
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Framing, Source}
import akka.util.ByteString

import scala.concurrent.Future
import scala.io.StdIn

trait FileUploader[Req, Res] extends (Source[Req, Any] => Future[Res])

object FileUploader {

  implicit def textFileUploader(implicit m: ActorMaterializer) = new FileUploader[ByteString, Int] {
    override def apply(v1: Source[ByteString, Any]): Future[Int] = {
      v1.via(Framing.delimiter(ByteString("\n"), 1024))
        .mapConcat(_.utf8String.split(",").toVector)
        .map { f =>
          println(f)
          f.toInt
        }
        .runFold(0) { (acc, n) => acc + n }
    }
  }
}

case class FileUploadRoute()(implicit uploader: FileUploader[ByteString, Int]) extends Route {
  override def apply(v1: RequestContext): Future[RouteResult] = route(v1)

  private val route: Route = fileUpload("csv") { case (_, byteSource) => onSuccess(uploader(byteSource)) { sum => complete(s"Sum: $sum") } }
}

object HttpServer extends App {
  implicit val system = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher

  val route =
    path("upload") {
      post {
        FileUploadRoute()
      }
    }

  val bindingFuture = Http().bindAndHandle(route, "localhost", 8080)

  println(s"Server online at http://localhost:8080/\nPress RETURN to stop...")
  StdIn.readLine() // let it run until user presses return
  bindingFuture
    .flatMap(_.unbind()) // trigger unbinding from the port
    .onComplete(_ => system.terminate()) // and shutdown when done
}


