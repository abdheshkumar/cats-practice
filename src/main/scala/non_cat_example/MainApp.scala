package non_cat_example

import instances.applicative._

object MainApp extends App {
  val username: Option[String]                           = Some("userName")
  val password: Option[String]                           = Some("userName")
  val url: Option[String]                                = Some("url")
  val attemptConnect: (String, String, String) => String = (_ + _ + _)

  val result1 = Applicative[Option]
    .map3(username, password, url)(attemptConnect)

  println(result1)

}
