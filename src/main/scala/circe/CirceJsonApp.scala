package circe
import io.circe.generic.extras.Configuration

object CirceJsonApp extends App {
//JSON Literal
  import io.circe.{Encoder, Json}
  import io.circe.literal.JsonStringContext
  json"null"
}
