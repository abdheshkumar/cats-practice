package akkahttpcirce

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait AkkaApp {
  implicit val system       = ActorSystem("my-system")
  implicit val materializer = ActorMaterializer()
}
