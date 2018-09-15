package case_study

import cats.{Applicative, Id}
import cats.implicits._

object CaseStudy1 extends App {
  def testTotalUptime() = {
    val hosts    = Map("host1" -> 10, "host2" -> 6)
    val client   = new TestUptimeClient(hosts)
    val service  = new UptimeService[Id](client)
    val actual   = service.getTotalUptime(hosts.keys.toList)
    val expected = hosts.values.sum
    assert(actual == expected)
  }
  testTotalUptime()
}

trait UptimeClient[F[_]] {
  def getUptime(hostname: String): F[Int]
}

class UptimeService[F[_]](client: UptimeClient[F])(implicit A: Applicative[F]) {
  def getTotalUptime(hostNames: List[String]): F[Int] =
    hostNames.traverse(client.getUptime).map(_.sum)
}

class TestUptimeClient(hosts: Map[String, Int]) extends UptimeClient[Id] {
  def getUptime(hostname: String): Int = hosts.getOrElse(hostname, 0)
}
