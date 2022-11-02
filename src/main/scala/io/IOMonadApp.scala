package io

import cats.effect.IO
import cats.effect.unsafe.IORuntime
object IOMonadApp extends App {
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
}
