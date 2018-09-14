/*
package elastic

import com.sksamuel.elastic4s.ElasticsearchClientUri
import com.sksamuel.elastic4s.http.HttpClient
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.client.config.RequestConfig
import org.apache.http.impl.client.BasicCredentialsProvider
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder
import org.elasticsearch.client.RestClientBuilder.HttpClientConfigCallback

trait ElasticClient extends App {
  lazy val provider = {
    val provider    = new BasicCredentialsProvider
    val credentials = new UsernamePasswordCredentials("elastic", "abdhesh")
    provider.setCredentials(AuthScope.ANY, credentials)
    provider
  }
  val client = ElasticsearchJavaRestClient(
    ElasticsearchClientUri("localhost", 9200),
    (requestConfigBuilder: RequestConfig.Builder) => requestConfigBuilder,
    new HttpClientConfigCallback {
      override def customizeHttpClient(
                          httpClientBuilder: HttpAsyncClientBuilder
      ): HttpAsyncClientBuilder =
        httpClientBuilder.setDefaultCredentialsProvider(provider)
    }
  )
}
 */
