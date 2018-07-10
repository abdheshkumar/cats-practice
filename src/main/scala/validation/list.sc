import cats.Monad
import cats.data.{NonEmptyList, Validated, ValidatedNel}
import cats.implicits._

case class User(id: String)

val users = List(User("1"), User("1"), User("1"), User("2"))
users.foldMap(u => Map(u.id -> {
  NonEmptyList(u, Nil)
}))

def validateUserByIndex(u: User, index: Int) = {
  Either.cond(u.id.nonEmpty, u, "invalid").toValidated.toValidatedNel
}


def validateUser(u: User) = {
  Either.cond(u.id.nonEmpty, u, "invalid").toValidated.toValidatedNel
}


def validateV(u: User): Validated[String, User] = {
  Either.cond(u.id.nonEmpty, u, "invalid").toValidated
}

def validateV1(u: User): Validated[String, User] = {
  Either.cond(u.id.length > 3, u, "invalid").toValidated
}

def validateVV(u: String): Validated[String, String] = {
  Either.cond(u.nonEmpty, u, "invalid").toValidated
}

def validateVV1(u: String): Validated[String, String] = {
  Either.cond(u.length > 3, u, "invalid").toValidated
}


/*
val c: Validated[String, String] = for {
  a <- validateVV("1")
  b <- validateVV1(a)
} yield b
*/

val d: Validated[String, String] =
  validateVV("1").withEither(_.flatMap(a => validateVV1(a).toEither))

def validateUsers(us: List[User]): List[ValidatedNel[String, User]] = {
  us.map(u => Either.cond(u.id.nonEmpty, u, "invalid").toValidated.toValidatedNel)
}

validateUsers(users).sequence
users.traverse(validateUser)

users.zipWithIndex.foldLeft(List.empty[User].validNel[String]) {
  case (v, (u, i)) =>
    val v1 = validateUserByIndex(u, i).map(_ :: Nil)
    v.combine(v1)
}



validateV(User("")).withEither(f => f.flatMap(a => validateV1(a).toEither))
