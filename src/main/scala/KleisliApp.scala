import cats.data.Kleisli
import cats.effect.IO
object KleisliApp extends App {

  val getNumberFromDb: Unit => Int    = _ => 2
  val processNumber: Int => Int       = _ * 2
  val writeNumberToDB: Int => Boolean = _ => true

  val composeFun: Unit => Boolean  = _ => writeNumberToDB(processNumber(getNumberFromDb())) //Not so readable
  val composeFun1: Unit => Boolean = writeNumberToDB compose processNumber compose getNumberFromDb
  val composeFun2: Unit => Boolean = getNumberFromDb andThen processNumber andThen writeNumberToDB

  //Sometime you want your output in some context to delay your processing or want to run program in effect

  val getNumberFromDbIO: Unit => IO[Int]    = _ => IO.pure(2)
  val processNumberIO: Int => IO[Int]       = number => IO(number * 2)
  val writeNumberToDBIO: Int => IO[Boolean] = _ => IO.delay(true)

  //val composeFun3: Unit => Boolean = getNumberFromDbIO andThen processNumberIO andThen writeNumberToDBIO // it would not compile

  def flatMap: Boolean = {
    val compose = getNumberFromDbIO().flatMap { dbNumber =>
      processNumberIO(dbNumber).flatMap { number =>
        writeNumberToDBIO(number)
      }
    }
    compose.unsafeRunSync()
  }
  //flatMap

  def forComprehension: Boolean = {
    val compose = for {
      dbNumber <- getNumberFromDbIO()
      number   <- processNumberIO(dbNumber)
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
}
