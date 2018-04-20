package functor

import java.util.{Date, UUID}

case class ContextMetadata(source: String, timestamp: GenerateTimestamp, uuid: GenerateUuid[String])

trait GenerateUuid[T] extends (() => T)

object GenerateUuid {
  implicit val uuid: GenerateUuid[String] = () => UUID.randomUUID().toString
}

trait GenerateTimestamp extends (() => String)

object GenerateTimestamp {
  def apply(implicit ts: GenerateTimestamp): GenerateTimestamp = ts
  implicit val ts: GenerateTimestamp = () => new Date().toString
}

object Main extends App {
}
