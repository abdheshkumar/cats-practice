package non_cat_example.instances

import non_cat_example.Functor

object functor {
  implicit val functorForOption: Functor[Option] = new Functor[Option] {
    def map[A, B](fa: Option[A])(f: A => B): Option[B] = fa match {
      case None    => None
      case Some(a) => Some(f(a))
    }
  }
  implicit val functorForList = new Functor[List] {
    override def map[A, B](fa: List[A])(f: A => B) = fa.map(f)
  }
}
