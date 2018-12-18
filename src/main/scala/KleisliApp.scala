import cats.data.Kleisli
import cats.Id
import cats.effect.IO
import cats.data.Kleisli
import cats.implicits._
object KleisliApp extends App {


  val getNumberFromDb: Unit => Int    = _ => 2
  val processNumber: Int => Int       = _ * 2
  val writeNumberToDB: Int => Boolean = _ => true

  val composeFun: Unit => Boolean  = _ => writeNumberToDB(processNumber(getNumberFromDb())) //Not so readable
  val composeFun1: Unit => Boolean = writeNumberToDB compose processNumber compose getNumberFromDb
  val composeFun2: Unit => Boolean = getNumberFromDb andThen processNumber andThen writeNumberToDB

  //Sometime you want your output in some context to delay your processing or want to run program in effect

  val getNumberFromDbIO: Unit => IO[Int]    = _ => IO.pure(2)
  val processNumberIOT: Int => IO[Int]       = number => IO(number * 2)
  val writeNumberToDBIO: Int => IO[Boolean] = _ => IO.delay(true)

  //val composeFun3: Unit => Boolean = getNumberFromDbIO andThen processNumberIO andThen writeNumberToDBIO // it would not compile

  def test(): Int        = 10
  def test2(v: Int): Int = v

  def flatMap: Boolean = {
    val compose = getNumberFromDbIO().flatMap { dbNumber =>
      processNumberIOT(dbNumber).flatMap { number => writeNumberToDBIO(number)
      }
    }
    compose.unsafeRunSync()
  }
  //flatMap

  def forComprehension: Boolean = {
    val compose = for {
      dbNumber <- getNumberFromDbIO()
      number   <- processNumberIOT(dbNumber)
      result   <- writeNumberToDBIO(number)
    } yield result
    compose.unsafeRunSync()
  }

  def kleisliComposition: Boolean = {
    val getNumberFromDbK: Kleisli[IO, Unit, Int]    = Kleisli.apply(_ => IO.pure(2))
    val processNumberK: Kleisli[IO, Int, Int]       = Kleisli.apply(number => IO(number * 2))
    val writeNumberToDBK: Kleisli[IO, Int, Boolean] = Kleisli.apply(_ => IO.delay(true))
    val compose: Kleisli[IO, Unit, Boolean]         = getNumberFromDbK andThen processNumberK andThen writeNumberToDBK
    getNumberFromDbK andThen processNumberIO andThen writeNumberToDBIO
    compose.run().unsafeRunSync()
  }




  //For Either
  type ET[A] = Either[String, A]
  val getDataFromDbEither: Kleisli[ET, Int, Int] =
    Kleisli[ET, Int, Int](id => Right(10))

  val processNumberEither: Kleisli[ET, Int, Int]     = Kleisli[ET, Int, Int](v => Right(v * 2))
  val writeDataToDBEither: Kleisli[ET, Int, Boolean] = Kleisli[ET, Int, Boolean](v => Right(true))

  val result = getDataFromDbEither andThen processNumberEither andThen writeDataToDBEither
  result.run(12) //Either[String,Boolean]

  //For Option
  val getDataFromDbOpt: Int => Option[Int]     = (id: Int) => Some(10)
  val processNumberOpt: Int => Option[Int]     = (v: Int) => Some(v * 2)
  val writeDataToDBOpt: Int => Option[Boolean] = (v: Int) => Some(true)

  val resultOpt = Kleisli(getDataFromDbOpt) andThen Kleisli(processNumberOpt) andThen Kleisli(
    writeDataToDBOpt)
  resultOpt.run(12) //Option[Boolean]

  //For Id monad
  val getDataFromDbFun: Kleisli[Id, Int, Int]     = Kleisli.apply(id => 10: Id[Int]) // I did because compiler is not infering type
  val processNumberFun: Kleisli[Id, Int, Int]     = Kleisli.apply(v => (v * 2): Id[Int])
  val writeDataToDBFun: Kleisli[Id, Int, Boolean] = Kleisli.apply(v => true: Id[Boolean])

  val resultFunc = getDataFromDbFun andThen processNumberFun andThen writeDataToDBFun
  resultFunc.run(12) //Id[Boolean] which is Boolean

  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  //For Future
  val getDataFromDbFuture: Kleisli[Future, Int, Int] = Kleisli.apply(_ => Future.successful(10))
  val processNumberFuture: Kleisli[Future, Int, Int] = Kleisli.apply(v => Future.successful(v * 2))
  val writeDataToDBFuture: Kleisli[Future, Int, Boolean] =
    Kleisli.apply(_ => Future.successful(true))

  val resultFuture = getDataFromDbFuture andThen processNumberFuture andThen writeDataToDBFuture
  resultFuture.run(12) //Future[Boolean]

  //For IO
  val getDataFromDbIO: Kleisli[IO, Int, Int]     = Kleisli.apply(_ => IO.pure(10))
  val processNumberIO: Kleisli[IO, Int, Int]     = Kleisli.apply(v => IO(v * 2))
  val writeDataToDBIO: Kleisli[IO, Int, Boolean] = Kleisli.apply(_ => IO.pure(true))

  val resultIO = getDataFromDbIO andThen processNumberIO andThen writeDataToDBIO
  resultIO.run(12) /*IO[Boolean]*/.unsafeRunSync() //Boolean

  val anotherFunction: Int => Kleisli[IO, Int, Int] = (v: Int) => Kleisli.liftF(processNumberIO(v))
  val oneMoreFunction: Int => Kleisli[IO, Int, Boolean] = (v: Int) =>
    Kleisli.liftF(writeDataToDBIO(v))

  val resultOfFor: Kleisli[IO, Int, Boolean] = for {
    v  <- getDataFromDbIO
    vv <- anotherFunction(v)
    r  <- oneMoreFunction(vv)
  } yield r
  resultOfFor(12).unsafeRunSync()
}
