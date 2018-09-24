package elastic

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.client.RequestBuilding
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse}
import akka.stream.ActorMaterializer

import scala.concurrent.Future
import scala.util.{Failure, Success}

object PartialUpdate extends App {

  def requestUrl(userId: String) = s"http://10.0.0.34:9200/test/example/$userId/_update"

  def addTagRequestBody(tag: String): String = {
    val permissionTag = tag
    s"""
       |{
       |    "script" : {
       |        "source": "if (ctx._source.permissionTags == null) { ctx._source.permissionTags = [];ctx._source.permissionTags.add(params.tag); } else if (!ctx._source.permissionTags.contains(params.tag)) {ctx._source.permissionTags.add(params.tag);} else { ctx.op = 'noop' }",
       |        "lang": "painless",
       |        "params" : {
       |            "tag" : "$permissionTag"
       |        }
       |    }
       |}
    """.stripMargin
  }

  def deleteTagRequestBody(tag: String): String = {
    s"""
       |{
       |    "script" : {
       |        "source": "ctx._source.tags.removeAll(Collections.singleton(params.tag))",
       |        "lang": "painless",
       |        "params" : {
       |            "tag" : "${tag}"
       |        }
       |    }
       |}
    """.stripMargin
  }

  implicit val system       = ActorSystem("src/main/elasticn/elastic")
  implicit val materializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext = system.dispatcher
  println(addTagRequestBody(">_429/5e1cb1ab0c0c49ed95c3b72117c96c93"))
  val entity = HttpEntity(
    ContentTypes.`application/json`,
    addTagRequestBody(">_429/5e1cb1ab0c0c49ed95c3b72117c96c93")
  )
  val req = RequestBuilding.Post(requestUrl("8"), entity)

  val responseFuture: Future[HttpResponse] = Http().singleRequest(req)

  responseFuture
    .onComplete {
      case Success(res) => println(res)
      case Failure(_)   => sys.error("something wrong")
    }

}
