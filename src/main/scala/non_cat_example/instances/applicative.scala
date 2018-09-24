package non_cat_example.instances

import non_cat_example.Applicative

object applicative {
  implicit val applicativeForOption = new Applicative[Option] {
    override def ap[A, B](fa: Option[A])(ff: Option[A => B]): Option[B] =
      for {
        f <- ff
        a <- fa
      } yield f(a)

    override def pure[A](a: A) = Some(a)
  }

  implicit val applicativeForList = new Applicative[List] {
    override def ap[A, B](fa: List[A])(ff: List[A => B]): List[B] =
      for {
        f <- ff
        a <- fa
      } yield f(a)

    override def pure[A](a: A) = List(a)
  }
}
