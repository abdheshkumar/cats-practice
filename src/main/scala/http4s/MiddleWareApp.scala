package http4s

import cats.Applicative
import cats.data.Kleisli
import cats.effect._
import cats.syntax.all._
import org.http4s._
//import org.http4s.circe.CirceEntityDecoder._
import org.http4s.server.blaze.BlazeBuilder
import org.http4s.server.middleware.{CORS, GZip}
import org.log4s.getLogger

import scala.language.higherKinds

object SFSessionCreation {
  def apply[F[_], G[_]](http: HttpRoutes[F])(implicit G: Sync[F]): HttpRoutes[F] = Kleisli {
    req: Request[F] =>
      req.uri match {
        case uri if uri.path.endsWith("/verify_otp") || uri.path.endsWith("/login") =>
          http(req).flatMapF(filterSensitiveInfo(_)) //F[G[Response[G]]]
        case _ => http(req)
      }
  }

  def filterSensitiveInfo[G[_]: Sync](response: Response[G]): G[Option[Response[G]]] = {
    response match {
      case Status.Successful(resp) =>
        resp
          .as[String]
          .map(_.reverse)
          .map(resp.withEntity(_))
          .map(r => Some.apply(r))
      case resp => Option(resp).pure[G]
    }
  }
}

object PrintReq {

  private[PrintReq] val logger = getLogger

  def apply[F[_], G[_]](
                         http: Http[F, G]
                       )(implicit F: Applicative[F], G: Sync[G]): Http[F, G] = Kleisli { req =>
    logger.info(
      s"received request url =>  ${req.uri} method => ${req.method} params => ${req.params}"
    )
    http(req)
  }
}

object BFMiddleWare {

  def apply[F[_], G[_]](http: Http[F, G])(implicit F: Applicative[F], G: Sync[G]): Http[F, G] =
    CORS(GZip(PrintReq(http)))
}

object MiddleWareApp extends IOApp {

  def app[F[_]: ConcurrentEffect]: fs2.Stream[F, ExitCode] = {

    val services: HttpRoutes[F] = ???

    BlazeBuilder[F]
      .bindHttp(8083, "0.0.0.0")
      .mountService(BFMiddleWare(services) /*Input Http[F, G] and output Http[F, G]*/, "/api")
      .serve
  }

  override def run(args: List[String]): IO[ExitCode] =
    app[IO].compile.drain.as(ExitCode.Success)
}
