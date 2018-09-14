package error

import scala.util.control.NoStackTrace

object ErrorApp extends App {

  case class A(message: String) extends Throwable

  case class B(m: String) extends NoStackTrace

  println(B("sas"))
}
