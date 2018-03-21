package elastic
import com.sksamuel.elastic4s.{ElasticsearchClientUri, RefreshPolicy}
import com.sksamuel.elastic4s.http.{HttpClient, RequestFailure, RequestSuccess}
import com.sksamuel.elastic4s.http.search.SearchResponse
object HttpClientExampleApp extends ElasticClient {

  // you must import the DSL to use the syntax helpers
  import com.sksamuel.elastic4s.http.ElasticDsl._

  client.execute {
    bulk(
      indexInto("myindex" / "mytype").fields("country" -> "Mongolia", "capital" -> "Ulaanbaatar"),
      indexInto("myindex" / "mytype").fields("country" -> "Namibia", "capital" -> "Windhoek")
    ).refresh(RefreshPolicy.WAIT_UNTIL)
  }.await

  val result: Either[RequestFailure, RequestSuccess[SearchResponse]] = client.execute {
    search("myindex").matchQuery("capital", "ulaanbaatar")
  }.await
  result.map{
    out=>
      println(out.foreach(_.hits))
  }

  // prints out the original json


  client.close()
}
