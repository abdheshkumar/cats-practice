package http4s

import cats.data.{Kleisli, OptionT}
import cats.{~>, Monad}
import org.http4s.dsl.Http4sDsl
import org.http4s.server.blaze.BlazeServerBuilder

import scala.concurrent.ExecutionContext.global
//import org.http4s.circe.CirceEntityDecoder._
import cats.effect._
import cats.implicits._
import org.http4s._
import org.http4s.implicits._
import org.http4s.dsl.io._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware.{CORS, GZip}

object TransFormResponse {
  def apply[F[_]: Monad, G[_]](http: Http[F, G], fk: G ~> F)(implicit G: Sync[G]): Http[F, G] =
    Kleisli { req: Request[G] =>
      http(req).flatMap(res => transformBody(res, fk))
    }

  def transformBody[F[_], G[_]: Sync](response: Response[G], fk: G ~> F): F[Response[G]] = fk {
    response match {
      case Status.Successful(resp) =>
        resp
          .as[String]
          .map(_.reverse) //Performing an operation on received body. here we can decode body and convert into json
          .map(resp.withEntity(_))
      case resp => resp.pure[G]
    }
  }
}

object BFMiddleWare {

  def apply[F[_], G[_]](
                      http: Http[F, G],
                      fk: G ~> F
  )(implicit F: Sync[F], G: Sync[G]): Http[F, G] =
    CORS(GZip(TransFormResponse(http, fk)))

}

class MyService[F[_]: Effect] extends Http4sDsl[F] {
  def myService: HttpRoutes[F] = HttpRoutes.of[F] {
    case GET -> Root / "hello" / name =>
      Ok(s"Hello, $name.")
  }
}

object MiddleWareApp extends IOApp {

  def app[F[_]: ConcurrentEffect](
                      implicit T: Timer[F],
                      C: ContextShift[F]): fs2.Stream[F, ExitCode] = {

    val httpApp: Http[OptionT[F, *], F] = BFMiddleWare(new MyService[F].myService, OptionT.liftK[F])

    BlazeServerBuilder[F]
      .bindHttp(8083, "0.0.0.0")
      .withHttpApp(httpApp.orNotFound)
      .serve
  }

  override def run(args: List[String]): IO[ExitCode] =
    app[IO].compile.drain.as(ExitCode.Success)
}
