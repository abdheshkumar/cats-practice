
package elastic

import com.sksamuel.elastic4s.http.{ElasticClient, ElasticProperties}
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider

trait ElasticClient extends App {
  lazy val provider = {
    val provider = new BasicCredentialsProvider
    val credentials = new UsernamePasswordCredentials("elastic", "abdhesh")
    provider.setCredentials(AuthScope.ANY, credentials)
    provider
  }

  val client = ElasticClient(ElasticProperties("http://localhost:9200"))
}

