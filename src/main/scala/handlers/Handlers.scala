package handlers

import scala.concurrent.Future

object PresenterHandler {
  implicit val presenterHandler = new algebra.Presenter.Handler[Future] {
    def show(id: Int): Future[Int] = Future.successful(12)
  }
}
