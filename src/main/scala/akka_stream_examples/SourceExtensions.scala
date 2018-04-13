package akka_stream_examples

import akka.stream.Materializer
import akka.stream.scaladsl.{Sink, Source}

import scala.concurrent.duration.FiniteDuration

object SourceExtensions {

  implicit class RichSource[Out, Mat](val source: Source[Out, Mat]) extends AnyVal {
    def throttle(duration: FiniteDuration) = source.zip(Sources.ticks(duration)).map(_._1)

    def hot(implicit mat: Materializer) = {
      val (actorRef, hotSource) = Connector.coupling[Out]()
      source.runForeach(x => actorRef ! x)
      hotSource
    }

    def multicast(implicit mat: Materializer) =
      Source.fromPublisher(source.runWith(Sink.asPublisher(true)))
  }

}
