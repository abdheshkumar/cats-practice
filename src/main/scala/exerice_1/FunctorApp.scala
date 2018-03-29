package exerice_1

object FunctorApp extends App {
  val a: Error = CustomError1
  a match {
    case CustomError1 => "dd"
    case CustomError2 =>
  }
}
