import cats.data.Reader
import cats.implicits._

case class Db(usernames: Map[Int, String], passwords: Map[String, String])

object Data {

  type DbReader[A] = Reader[Db, A]

  def findUsername(userId: Int): DbReader[Option[String]] = Reader(db => db.usernames.get(userId))

  def checkPassword(username: String, password: String): DbReader[Boolean] =
    Reader(db => db.passwords.get(username).contains(password))

  def checkLogin(userId: Int, password: String): DbReader[Boolean] =
    for {
      person <- findUsername(userId)
      passWordRight <- person.map(p => checkPassword(p, password)).getOrElse {
        false.pure[DbReader]
      }
    } yield passWordRight

  val users = Map(
    1 -> "dade",
    2 -> "kate",
    3 -> "margo"
  )

  val passwords = Map("dade" -> "zerocool", "kate" -> "acidburn", "margo" -> "secret")

  val db = Db(users, passwords)
}

object Main {

  def main(args: Array[String]): Unit = {
    println(Data.findUsername(1).run(Data.db))
    println(Data.checkPassword("dade", "zerocool").run(Data.db))
    println(Data.checkLogin(1, "secret").run(Data.db))
  }
}
