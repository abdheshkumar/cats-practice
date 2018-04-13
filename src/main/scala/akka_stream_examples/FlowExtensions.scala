package akka_stream_examples

import akka.stream.scaladsl.Flow

import scala.concurrent.duration.FiniteDuration

object FlowExtensions {

  implicit class RichFlow[In, Out, Mat](val flow: Flow[In, Out, Mat]) extends AnyVal {
    def throttle(duration: FiniteDuration) = flow.zip(Sources.ticks(duration)).map(_._1)
  }

}
