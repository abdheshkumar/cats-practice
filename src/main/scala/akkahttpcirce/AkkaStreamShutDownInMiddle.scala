package akkahttpcirce

import akka.NotUsed
import akka.stream.scaladsl.Source
import akka.stream.{Graph, SourceShape}

import scala.concurrent.Future

object AkkaStreamShutDownInMiddle extends App with AkkaApp {
 /* val countingSrc = Source(Stream.from(1)).delay(1.second, DelayOverflowStrategy.backpressure)

  val result = countingSrc
    .viaMat(new FirstValue[Int])(Keep.right)
    //un()*/
 // A GraphStage is a proper Graph, just like what GraphDSL.create would return
 val sourceGraph: Graph[SourceShape[Int], NotUsed] = new NumbersSource

  // Create a Source from the Graph to access the DSL
  val mySource: Source[Int, NotUsed] = Source.fromGraph(sourceGraph)

  // Returns 55
  val result1: Future[Int] = mySource.take(10).runFold(0)(_ + _)

  // The source is reusable. This returns 5050
  val result2: Future[Int] = mySource.take(100).runFold(0)(_ + _)
}
