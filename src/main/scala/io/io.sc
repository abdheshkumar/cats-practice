import cats.effect.IO
import cats.effect.unsafe.IORuntime
implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global
val ioa = IO {
  println("hey!")
}

val program: IO[Unit] =
  for {
    _ <- ioa
    _ <- ioa
  } yield ()

program.unsafeRunSync()

def fib(n: Int, a: Long = 0, b: Long = 1): IO[Long] =
  IO(a + b).flatMap { b2 =>
    if (n > 0)
      fib(n - 1, b, b2)
    else
      IO.pure(b2)
  }

fib(200000000,0,1)