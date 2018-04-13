package akka_stream_examples.utils

import scala.concurrent.duration.Duration
import scala.concurrent.{Future, Promise}
import scala.util.Try

object Scheduler {

  def asFuture[T](delay: Duration)(block: => T): Future[T] = {

    val promise = Promise[T]()

    Config.threadPool.schedule(
      new Runnable {
        def run() = promise.complete(Try(block))
      },
      delay.length,
      delay.unit
    )
    promise.future
  }
}