package akka_stream_examples.utils

import java.util.concurrent.{Executors, ScheduledExecutorService}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, ExecutionContextExecutorService}

object Config extends Config(numberOfThreads = 4)

object BlockingConfig extends Config(numberOfThreads = 4)

class Config(numberOfThreads: Int) {

  val threadPool: ScheduledExecutorService =
    Executors.newScheduledThreadPool(numberOfThreads)

  implicit val executionContext: ExecutionContextExecutorService =
    ExecutionContext.fromExecutorService(threadPool)

  implicit val system = ActorSystem("Sony")

  implicit val mat = ActorMaterializer()
}
