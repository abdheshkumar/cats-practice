package parallel

object ParallelApp extends App {
  import cats.implicits._

  import cats.data._
  case class Name(value: String)
  case class Age(value: Int)
  case class Person(name: Name, age: Age)
  type Response[A] = Either[NonEmptyList[String], A]
  def parse(s: String): Response[Int] =
    if (s.matches("-?[0-9]+")) Right(s.toInt)
    else Left(NonEmptyList.one(s"$s is not a valid integer."))

  def validateAge(a: Int): Response[Age] =
    if (a > 18) Right(Age(a))
    else Left(NonEmptyList.one(s"$a is not old enough"))

  def validateName(n: String): Response[Name] =
    if (n.length >= 8) Right(Name(n))
    else Left(NonEmptyList.one(s"$n Does not have enough characters"))

  def parsePerson(ageString: String, nameString: String) =
    for {
      age <- parse(ageString)
      person <- (validateName(nameString).toValidated, validateAge(age).toValidated)
        .mapN(Person)
        .toEither
    } yield person

  println(parsePerson("2", "sa"))

  def parsePersonParallel(ageString: String, nameString: String): Response[Person] =
    for {
      age    <- parse(ageString)
      person <- (validateName(nameString), validateAge(age)).parMapN(Person)
    } yield person

  println(parsePersonParallel("2", "sa"))
}
