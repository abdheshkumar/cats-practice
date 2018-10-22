import cats.MonadError

import scala.concurrent.Future
object CatsAssignment extends App {
  import cats.data.Validated
  class Document {
    def setTitle(title: String): this.type   = { {}; this }
    def setAuthor(author: String): this.type = { {}; this }
  }

  class Book extends Document {
    def addChapter(chapter: String) = { {}; this }
  }
  new Book().setTitle("")

  type Metadata = Map[String, String]

  object Metadata {
    def apply(data: (String, String)*): Metadata = data.toMap
  }

  Metadata("Hello" -> "World")
  import cats.implicits._
  import scala.concurrent.ExecutionContext.Implicits.global
  import com.olegpy.meow.hierarchy._ // All you need is this import
  sealed trait UserError                         extends Exception
  case class UserAlreadyExists(username: String) extends UserError
  case class UserNotFound(username: String)      extends UserError
  case class InvalidUserAge(age: Int)            extends UserError
  implicitly[MonadError[Future, UserError]]
}
case class Address()
case class User()
