
package elastic

import com.sksamuel.elastic4s.http.JavaClient
import com.sksamuel.elastic4s.{ElasticClient, ElasticProperties}
import org.apache.http.auth.{AuthScope, UsernamePasswordCredentials}
import org.apache.http.impl.client.BasicCredentialsProvider

trait Elastic4sClient extends App {
  lazy val provider = {
    val provider = new BasicCredentialsProvider
    val credentials = new UsernamePasswordCredentials("elastic", "abdhesh")
    provider.setCredentials(AuthScope.ANY, credentials)
    provider
  }

  val client = ElasticClient(JavaClient(ElasticProperties("http://localhost:9200")))
}

