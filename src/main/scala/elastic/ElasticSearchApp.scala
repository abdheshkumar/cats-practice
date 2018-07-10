package elastic

import com.sksamuel.elastic4s.IndexAndTypes
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.http.search.SearchBodyBuilderFn

object ElasticSearchApp extends ElasticClient {
  val queries = searchWithType(IndexAndTypes("readmodels", "service_numbers"))
    .query(
      boolQuery()
        .should(termQuery("number", 32323), termQuery("numbeewer", 43432323))
    )
  //.query(constantScoreQuery(termsQuery("number", 32323, 343434)))
  /*.termQuery("number", 32323)
    .termQuery("test", 3343)*/

  println(SearchBodyBuilderFn(queries).string())
  /*val result = client.execute(multi(queries)).await
  result.map {
    out =>
      println(out)
  }*/
}
