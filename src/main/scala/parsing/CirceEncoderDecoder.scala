package parsing

import io.circe.generic.extras._
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import io.circe.generic.auto._
import java.text.SimpleDateFormat
import java.util.Date
import scala.util.Try

object CirceEncoderDecoder extends App {


  @ConfiguredJsonCodec case class Bar(@JsonKey("my-int") i: Int, s: String, d: Date)

  object Bar {
    private[this] def fmt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ")

    implicit val config: Configuration = Configuration.default
    implicit val dateEncoder: Encoder[Date] = Encoder[String].contramap(fmt.format)
    implicit val dateDecoder: Decoder[Date] = Decoder[String].emapTry(str => Try(fmt.parse(str)))
  }

  println(Bar(13, "Qux", new Date).asJson)

}
