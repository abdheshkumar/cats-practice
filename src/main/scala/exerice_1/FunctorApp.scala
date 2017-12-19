package exerice_1

import cats.data.State

object FunctorApp {
  val func = (x: Int) => x + 1

  val optFunc: Option[Int] => Option[Int] = (a: Option[Int]) => a.map(func)

  case class Master(workers: Map[String, Worker])
  case class Worker(elapsed: Long, result: Vector[String])
  case class Message(workerId: String, work: String, elapsed: Long)


  type WorkerState[A] = State[Worker, A]

  def update(message: Message): WorkerState[Unit] = State.modify { w =>
    w.copy(
      elapsed = w.elapsed + message.elapsed,
      result = w.result :+ message.work
    )
  }

  //def getWork: WorkerState[Vector[String]] = State.get
  //def getElapsed: WorkerState[Long] = State.gets(_.elapsed)
  /*def updateAndGetElapsed(message: Message): WorkerState[Long] = for {
    _ <- update(message)
    elapsed <- getElapsed
  } yield elapsed

  val r = updateAndGetElapsed(Message("workerId","Work",12345))
  println(r.run)*/
}
