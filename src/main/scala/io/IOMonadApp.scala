package io

import cats.effect.IO

object IOMonadApp extends App {

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
