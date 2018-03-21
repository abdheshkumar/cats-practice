package elastic

import com.sksamuel.elastic4s.IndexAndTypes
import com.sksamuel.elastic4s.http.ElasticDsl._

object ElasticSearchApp extends ElasticClient {
  val queries = List[Long]().map {
    number =>println(":::::")
      searchWithType(IndexAndTypes("readmodels", "service_numbers"))
        .termQuery("number", number)
        .sourceInclude("id", "number")
  }
  val result = client.execute(multi(queries)).await
  result.map {
    out =>
      println(out)
  }
}
