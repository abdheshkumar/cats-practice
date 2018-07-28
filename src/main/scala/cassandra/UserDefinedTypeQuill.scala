package cassandra

import io.getquill.context.cassandra.Udt

import io.getquill.{CassandraMirrorContext, SnakeCase}

object UserDefinedTypeQuill extends App {


  val ctx = new CassandraMirrorContext(SnakeCase)

  import ctx._

  case class B(s: String) extends Udt

  case class A(s: List[B])

  val r = quote {
    query[A].filter(_.s == lift(List(B(""))))
  }
  val rr = run(r)
  println(rr.string) //SELECT s FROM a WHERE s = ?
}
