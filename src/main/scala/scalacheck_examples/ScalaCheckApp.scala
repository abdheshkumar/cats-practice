package scalacheck_examples

import org.scalacheck.Arbitrary

object ScalaCheckApp extends App {

  import org.scalacheck.Gen
  import org.scalacheck.Prop.forAll

  // strGen generates a fixed length random string
  val strGen = (n: Int) => Gen.listOfN(n, Gen.alphaChar).map(_.mkString)

  val fixedLengthStr = forAll(strGen(10)) { s =>
    s.length == 10
  }

  fixedLengthStr.check

  case class Account(accountId: String, balance: Double, country: String)

  case class Customer(
                      customerId: String,
                      name: String,
                      nationality: String,
                      accounts: Seq[Account]
  )

  // Account generator - only Benelux accounts
  val genAccount = for {
    accountId <- Gen.identifier
    balance   <- Arbitrary.arbitrary[Double]
    country   <- Gen.oneOf("NL", "BE", "LU")
  } yield Account(accountId, balance, country)

  val account = forAll(genAccount) { s =>
    List("NL", "BE", "LU").contains(s.country) == true
  }
  account.check

  // Forcing customers to be Dutch will be as easy as:
  val genDutchCustomer = for {
    customerId  <- Gen.identifier
    name        <- Arbitrary.arbitrary[String].suchThat(_.nonEmpty)
    nationality <- Gen.const("NL")
    accounts    <- Gen.nonEmptyListOf(genAccount)
  } yield Customer(customerId, name, nationality, accounts)

  val customer = forAll(genDutchCustomer) { s =>
    s.nationality == "NL"
  }
  customer.check

  sealed trait Tree[A]

  case class Node[A](left: Tree[A], right: Tree[A]) extends Tree[A]

  case class Leaf[A](value: A) extends Tree[A]

  import org.scalacheck._
  import Arbitrary.arbitrary
  import Gen._

  def genLeaf[A: Arbitrary]: Gen[Leaf[A]] = arbitrary[A].map(Leaf(_))

  def genNode[A: Arbitrary]: Gen[Node[A]] =
    for {
      //v     <- arbitrary[Int]
      left  <- genTree[A]
      right <- genTree[A]
    } yield Node(left, right)

  def genTree[A: Arbitrary]: Gen[Tree[A]] = oneOf(genLeaf[A], genNode[A])

  val genStringStream: Gen[Stream[String]] =
    Gen.containerOf[Stream, String](Gen.alphaStr)
  println(genStringStream.sample)

  val evenInteger = Arbitrary.arbitrary[Int] suchThat (_ % 2 == 0)

  val squares = for {
    xs <- Arbitrary.arbitrary[List[Int]]
  } yield xs.map(x => x * x)

  println(evenInteger.sample)

  case class Foo(intValue: Int, charValue: Char)

  import org.scalacheck.Gen
  import org.scalacheck.Prop.forAll

  val fooGen = for {
    intValue  <- Gen.posNum[Int]
    charValue <- Gen.alphaChar
  } yield Foo(intValue, charValue)

  val check = {
    forAll(fooGen) { foo =>
      foo.intValue > 0 && foo.charValue.isDigit == false
    }
  }
  check.check

  val genIntList: Gen[List[Int]] =
    Gen.containerOf[List, Int](Gen.oneOf(1, 3, 5))

  val genBoolArray: Gen[Array[Boolean]] = Gen.containerOf[Array, Boolean](true)
  val strGenAl = for {
    firstName   <- Gen.alphaStr
    lastName    <- Gen.alphaStr
    email       <- Gen.alphaStr
    emailDomain <- Gen.oneOf("callhandling.co.uk", "gmail.com")
  } yield s"$firstName,$lastName,$email@$emailDomain"

  val csvData = Gen.listOfN(5, strGenAl)
  println(csvData.sample.toList.flatten.mkString("\n"))

  case class User(name: String)

  case class Person(name: String, age: Int, user: Option[User])

  implicit def arbOption: Arbitrary[Option[User]] =
    Arbitrary(Gen.option(Gen.resultOf(User)))

  val people = Gen.resultOf(Person) // case class trick - generate random instances if there are implicit generators for the constructor parameters:
  val p = forAll(people) { p =>
    println(p)
    true

  }
  p.check
  /*
  object Person {  }
  val people1 = Gen.resultOf(Person.apply _) //When you have companion object
   */
  println("People" + people.sample)

}
