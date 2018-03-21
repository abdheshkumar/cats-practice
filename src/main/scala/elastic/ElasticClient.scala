package elastic

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient

trait ElasticClient extends App{
  val client: HttpClient = HttpClient(ElasticsearchClientUri("localhost", 9200))
}
