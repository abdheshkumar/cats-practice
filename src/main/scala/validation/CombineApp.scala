package validation

object CombineApp extends App {

  import cats.implicits._

  import scala.concurrent.ExecutionContext.Implicits.global
  import scala.concurrent.Future

  val f1 = Future.successful("1")
  val f2 = Future.successful("1")
  val f3 = Future.failed[String](new Exception("failed"))
  val list: List[Future[String]] = List(f1, f3, f2)

  val res = list.sequence[Future, String]

}
