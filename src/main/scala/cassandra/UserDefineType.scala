package cassandra

import java.util.{Date, UUID}

import io.getquill.context.cassandra.Udt
import io.getquill.{CassandraAsyncContext, SnakeCase}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object UserDefineType extends App {
  val ctx = new CassandraAsyncContext(SnakeCase, "db")

  import ctx._

  /*
  CREATE TYPE cycling.basic_info (
    birthday timestamp,
    nationality text,
    weight text,
    height text
  );

  CREATE TABLE cycling.cyclist_stats ( id uuid PRIMARY KEY, lastname text, basics FROZEN<basic_info>);
   */
  case class BasicInfo(birthday: Date, nationality: String, weight: String, height: String)
      extends Udt

  val aa = implicitly[Encoder[BasicInfo]]

  case class CyclistStats(id: UUID, lastname: String, basics: Option[BasicInfo])

  val in = CyclistStats(
    id = UUID.randomUUID(),
    lastname = "last name",
    basics = Some(BasicInfo(new Date(), nationality = "", weight = "", height = ""))
  )
  val q = quote {
    query[CyclistStats].insert(lift(in))
  }
  println(aa.encoder)
  val r: Future[Unit] = run(q)
  r.onComplete { f =>
    println(f)
  }
}
