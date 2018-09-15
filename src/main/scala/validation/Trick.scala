package validation

//import cats.implicits._

object Trick {

  import cats.implicits._
  /*_*/
  val red = (1.asRight[String], 2.asRight[String]).mapN(_ + _)
  /*_*/

  /*_*/
  val not = (1.asRight, 2.asRight).mapN(_ + _)
  /*_*/
}
