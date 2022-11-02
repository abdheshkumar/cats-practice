package http4s

import cats.data.{Kleisli, OptionT}
import cats.effect._
import cats.syntax.all._
import cats.{ApplicativeError, MonadError}
import io.circe.generic.auto._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe._
import io.circe.syntax._
import org.http4s._
import org.http4s.dsl.Http4sDsl
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.server.middleware.Logger
import org.http4s.implicits._

case class User(username: String, age: Int)

case class UserUpdateAge(age: Int)

sealed trait UserError extends Throwable

case class UserAlreadyExists(username: String) extends UserError

case class UserNotFound(username: String) extends UserError

case class InvalidUserAge(age: Int) extends UserError

trait UserAlgebra[F[_]] {
  def find(username: String): F[Option[User]]

  def save(user: User): F[Unit]

  def updateAge(username: String, age: Int): F[Unit]
}

object UserInterpreter {

  def create[F[_]](implicit F: Sync[F]): UserAlgebra[F] = new UserAlgebra[F] {
    private val state = Ref.unsafe[F, Map[String, User]](Map.empty)

    private def validateAge(age: Int): F[Unit] =
      if (age <= 0) F.raiseError(InvalidUserAge(age)) else F.unit

    override def find(username: String): F[Option[User]] =
      state.get.map(_.get(username))

    override def save(user: User): F[Unit] =
      validateAge(user.age) *>
        find(user.username).flatMap {
          case Some(_) =>
            F.raiseError(UserAlreadyExists(user.username))
          case None =>
            state.update(_.updated(user.username, user))
        }

    override def updateAge(username: String, age: Int): F[Unit] =
      validateAge(age) *>
        find(username).flatMap {
          case Some(user) =>
            state.update(_.updated(username, user.copy(age = age)))
          case None =>
            F.raiseError(UserNotFound(username))
        }
  }

}

class UserRoutes[F[_]: Async](userAlgebra: UserAlgebra[F]) extends Http4sDsl[F] {

  import org.http4s.HttpRoutes

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "users" / username =>
      userAlgebra.find(username).flatMap {
        case Some(user) => Ok(user.asJson)
        case None       => NotFound(username.asJson)
      }

    case req @ POST -> Root / "users" =>
      req.as[User].flatMap { user =>
        userAlgebra.save(user) *> Created(user.username.asJson)
      }

    case req @ PUT -> Root / "users" / username =>
      req.as[UserUpdateAge].flatMap { userUpdate =>
        userAlgebra.updateAge(username, userUpdate.age) *> Ok(username.asJson)
      }
  }

}

class UserRoutesAlt[F[_]: Async](userAlgebra: UserAlgebra[F]) extends Http4sDsl[F] {

  val routes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "users" / username =>
      userAlgebra.find(username).flatMap {
        case Some(user) => Ok(user.asJson)
        case None       => NotFound(username.asJson)
      }

    case req @ POST -> Root / "users" =>
      req
        .as[User]
        .flatMap { user =>
          userAlgebra.save(user) *> Created(user.username.asJson)
        }
        .handleErrorWith { // compiles without giving you "match non-exhaustive" error
          case UserAlreadyExists(username) => Conflict(username.asJson)
        }

    case req @ PUT -> Root / "users" / username =>
      req
        .as[UserUpdateAge]
        .flatMap { userUpdate =>
          userAlgebra.updateAge(username, userUpdate.age) *> Ok(username.asJson)
        }
        .handleErrorWith { // compiles without giving you "match non-exhaustive" error
          case InvalidUserAge(age) => BadRequest(s"Invalid age $age".asJson)
        }
  }

}

trait HttpErrorHandler[F[_], E <: Throwable] {
  def handle(routes: HttpRoutes[F]): HttpRoutes[F]
}

object RoutesHttpErrorHandler {
  def apply[F[_]: ApplicativeError[*[_], E], E <: Throwable](
                      routes: HttpRoutes[F]
  )(handler: E => F[Response[F]]): HttpRoutes[F] =
    Kleisli { req =>
      OptionT {
        routes.run(req).value.handleErrorWith(e => handler(e).map(Option(_)))
      }
    }
}

object HttpErrorHandler {
  def apply[F[_], E <: Throwable](implicit ev: HttpErrorHandler[F, E]): HttpErrorHandler[F, E] = ev
}

class UserRoutesMTL[F[_]: Async](userAlgebra: UserAlgebra[F])(
                    implicit H: HttpErrorHandler[F, UserError]
) extends Http4sDsl[F] {

  private val httpRoutes: HttpRoutes[F] = HttpRoutes.of[F] {

    case GET -> Root / "users" / username =>
      userAlgebra.find(username).flatMap {
        case Some(user) => Ok(user.asJson)
        case None       => NotFound(username.asJson)
      }

    case req @ POST -> Root / "users" =>
      req.as[User].flatMap { user =>
        userAlgebra.save(user) *> Created(user.username.asJson)
      }

    case req @ PUT -> Root / "users" / username =>
      req.as[UserUpdateAge].flatMap { userUpdate =>
        userAlgebra.updateAge(username, userUpdate.age) *> Created(username.asJson)
      }
  }

  val routes: HttpApp[F] = H.handle(httpRoutes).orNotFound

}

class UserHttpErrorHandler[F[_]: ApplicativeError[*[_], Throwable]]
    extends HttpErrorHandler[F, UserError]
    with Http4sDsl[F] {
  private val handler: Throwable => F[Response[F]] = {
    case InvalidUserAge(age)         => BadRequest(s"Invalid age $age".asJson)
    case UserAlreadyExists(username) => Conflict(username.asJson)
    case UserNotFound(username)      => NotFound(username.asJson)
  }

  override def handle(routes: HttpRoutes[F]): HttpRoutes[F] =
    RoutesHttpErrorHandler(routes)(handler)
}

object Http2sApp extends IOApp {

  def app[F[_]: Async](): fs2.Stream[F, ExitCode] = {
    implicit def userHttpErrorHandler: HttpErrorHandler[F, UserError] = new UserHttpErrorHandler[F]
    val finalHttpApp =
      Logger.httpApp(true, true)(new UserRoutesMTL[F](UserInterpreter.create[F]).routes)
    BlazeServerBuilder[F]
      .bindHttp(8083, "0.0.0.0")
      .withHttpApp(finalHttpApp)
      .serve
  }

  override def run(args: List[String]): IO[ExitCode] =
    app[IO].compile.drain.as(ExitCode.Success)
}
