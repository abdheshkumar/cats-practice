
package akkahttpcirce

import akka.actor.ActorSystem
import akka.http.scaladsl.marshalling.Marshal
import akka.http.scaladsl.model.ContentTypes.`application/json`
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling.Unmarshaller.UnsupportedContentTypeException
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import cats.data.NonEmptyList
import io.circe.CursorOp.DownField
import io.circe.{DecodingFailure, Printer}
import org.scalatest.{AsyncWordSpec, BeforeAndAfterAll, Matchers}
import scala.collection.immutable
import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object CirceSupportSpec {

  final case class Foo(bar: String) {
    require(bar == "bar", "bar must be 'bar'!")
  }

  final case class MultiFoo(a: String, b: String)

  final case class OptionFoo(a: Option[String])

}

final class CirceSupportSpec extends AsyncWordSpec with Matchers with BeforeAndAfterAll {

  import CirceSupportSpec._

  private implicit val system: ActorSystem = ActorSystem()
  private implicit val mat: ActorMaterializer = ActorMaterializer()

  private val `application/json-home` =
    MediaType.applicationWithFixedCharset("json-home", HttpCharsets.`UTF-8`, "json-home")

  override protected def afterAll() = {
    Await.ready(system.terminate(), 42.seconds)
    super.afterAll()
  }

  "FailFastCirceSupport" should {
    import io.circe.generic.auto._

    behave like commonCirceSupport(FailFastCirceSupport)
    import FailFastCirceSupport._

    "fail-fast and return only the first unmarshalling error" in {
      val entity = HttpEntity(MediaTypes.`application/json`, """{ "a": 1, "b": 2 }""")
      val error = DecodingFailure("String", List(DownField("a")))
      Unmarshal(entity)
        .to[MultiFoo]
        .failed
        .map(_ shouldBe error)
    }

    "allow unmarshalling with passed in Content-Types" in {
      val foo = Foo("bar")

      final object CustomCirceSupport extends FailFastCirceSupport {
        override def unmarshallerContentTypes: immutable.Seq[ContentTypeRange] = List(`application/json`, `application/json-home`)
      }
      import CustomCirceSupport._

      val entity = HttpEntity(MediaTypes.`application/json`, """{ "bar": "bar" }""")
      Unmarshal(entity).to[Foo].map(_ shouldBe foo)
    }
  }

  "ErrorAccumulatingCirceSupport" should {
    import io.circe.generic.auto._
    import FailFastCirceSupport._

    //behave like commonCirceSupport(ErrorAccumulatingCirceSupport)

    "accumulate and return all unmarshalling errors" in {
      val entity = HttpEntity(MediaTypes.`application/json`, """{ "a": 1, "b": 2 }""")
      val errors =
        NonEmptyList.of(
          DecodingFailure("String", List(DownField("a")))
        )
      val errorMessage: String = ErrorAccumulatingCirceSupport.DecodingFailures(errors).getMessage
      Unmarshal(entity)
        .to[MultiFoo]
        .failed
        .map(_.getMessage shouldBe errorMessage)
    }

    "allow unmarshalling with passed in Content-Types" in {
      val foo = Foo("bar")

      final object CustomCirceSupport extends ErrorAccumulatingCirceSupport {
        override def unmarshallerContentTypes: immutable.Seq[ContentTypeRange] = List(`application/json`, `application/json-home`)
      }
      import CustomCirceSupport._

      val entity = HttpEntity(`application/json-home`, """{ "bar": "bar" }""")
      Unmarshal(entity).to[Foo].map(_ shouldBe foo)
    }
  }

  /**
    * Specs common to both [[akkahttpcirce.FailFastCirceSupport]] and [[akkahttpcirce.ErrorAccumulatingCirceSupport]]
    */
  private def commonCirceSupport(support: BaseCirceSupport): Unit = {
    import io.circe.generic.auto._
    import support._

    "enable marshalling and unmarshalling objects for generic derivation" in {
      val foo = Foo("bar")
      Marshal(foo)
        .to[RequestEntity]
        .flatMap(Unmarshal(_).to[Foo])
        .map(_ shouldBe foo)
    }

    "provide proper error messages for requirement errors" in {
      val entity = HttpEntity(MediaTypes.`application/json`, """{ "bar": "baz" }""")
      Unmarshal(entity)
        .to[Foo]
        .failed
        .map(_ should have message "requirement failed: bar must be 'bar'!")
    }

    "fail with NoContentException when unmarshalling empty entities" in {
      val entity = HttpEntity.empty(`application/json`)
      Unmarshal(entity)
        .to[Foo]
        .failed
        .map(_ shouldBe Unmarshaller.NoContentException)
    }

    "fail with UnsupportedContentTypeException when Content-Type is not `application/json`" in {
      val entity = HttpEntity("""{ "bar": "bar" }""")
      Unmarshal(entity)
        .to[Foo]
        .failed
        .map(_ shouldBe UnsupportedContentTypeException(`application/json`))
    }

    "write None as null by default" in {
      val optionFoo = OptionFoo(None)
      Marshal(optionFoo)
        .to[RequestEntity]
        .map(_.asInstanceOf[HttpEntity.Strict].data.decodeString("UTF-8") shouldBe "{\"a\":null}")
    }

    "not write None" in {
      implicit val printer: Printer = Printer.noSpaces.copy(dropNullValues = true)
      val optionFoo = OptionFoo(None)
      Marshal(optionFoo)
        .to[RequestEntity]
        .map(_.asInstanceOf[HttpEntity.Strict].data.decodeString("UTF-8") shouldBe "{}")
    }
  }
}
