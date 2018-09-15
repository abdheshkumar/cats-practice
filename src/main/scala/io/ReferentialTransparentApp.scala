package io
import cats.effect.{ExitCode, IOApp}

object ReferentialTransparentApp extends IOApp {
  import cats.implicits._
  import cats.effect.IO

  def compute1(): Int = { println("compute1"); 1 }
  def compute2(): Int = { println("compute2"); 2 }

  override def run(args: List[String]): IO[ExitCode] = {
    val r0 = for {
      a <- IO(compute1())
      b <- IO(compute2())
    } yield a + b

    r0.unsafeRunSync()

    val faIO = IO(compute1())
    val fbIO = IO(compute2())
    val r1 = for {
      a <- faIO
      b <- fbIO
    } yield a + b

    r1.unsafeRunSync()

    def fa0IO = IO(compute1())
    def fb0IO = IO(compute2())
    val r2 = for {
      a <- fa0IO
      b <- fb0IO
    } yield a + b

    r2.unsafeRunSync()

    //IO is referential transparent. val or def doesn't matter
    //If you want to run parallel instead? Do it explicitly

    val r3: IO[Int] = (fa0IO, fb0IO).parMapN(_ + _)
    r3.unsafeRunSync()
    IO.pure(ExitCode.Success)
  }
}
