package handlers


import scala.concurrent.Future

object Handlers {
  implicit val validationHandler = new algebra.Validation.Handler[Future] {
    override def minSize(s: String, n: Int): Future[Boolean] = Future.successful {
      s.size >= n
    }

    override def hasNumber(s: String): Future[Boolean] = Future.successful {
      s.exists(c => "0123456789".contains(c))
    }
  }

  implicit val interactionHandler = new algebra.Interaction.Handler[Future] {
    override def tell(s: String): Future[Unit] = Future.successful {
      println(s)
    }

    override def ask(s: String): Future[String] = Future.successful {
      println(s); "This could have been user input 1"
    }
  }

  implicit val presenterHandler = new algebra.Presenter.Handler[Future] {
    def show(id: Int): Future[Int] = Future.successful(12)
  }
}
