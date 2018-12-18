
package elastic

import com.sksamuel.elastic4s.IndexAndTypes
import com.sksamuel.elastic4s.http.ElasticDsl._
import com.sksamuel.elastic4s.json.XContentFactory
import com.sksamuel.elastic4s.searches.queries.term.TermsQuery

object ElasticSearchApp extends ElasticClient {
  val queries = searchWithType(IndexAndTypes("readmodels", "service_numbers"))
    .query(
      boolQuery()
        .should(termsQuery("number", List.empty[String]))
    )
  //.query(constantScoreQuery(termsQuery("number", 32323, 343434)))
  /*.termQuery("number", 32323)
    .termQuery("test", 3343)*/

  val t = TermsQuery[String]("fieldName", values = List.empty[String])
  val builder = XContentFactory.jsonBuilder().startObject("terms")

  builder.startArray(t.field)
  t.values.foreach(builder.autovalue)
  builder.endArray()

  println(builder.string())
  val result = client.execute(multi(queries)).await
  result.map {
    out =>
      println(out)
  }
}

