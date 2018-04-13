package akka_stream_examples

object TExtensions {

  implicit class RichT[T](val x: T) extends AnyVal {
    def log(prefix: String = "") = {
      println(s"$prefix: $x")
      x
    }
  }

}
