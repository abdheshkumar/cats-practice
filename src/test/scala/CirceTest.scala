import cats.kernel.Eq
import io.circe.generic.extras.Configuration
import io.circe.{Decoder, Encoder, Json}
import io.circe.generic.extras.auto._
import io.circe.literal._
import org.scalacheck.{Arbitrary, Gen}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

object localExamples {
  sealed trait ConfigExampleBase
  case class ConfigExampleFoo(thisIsAField: String, a: Int = 0, b: Double) extends ConfigExampleBase
  case object ConfigExampleBar                                             extends ConfigExampleBase

  object ConfigExampleFoo {
    implicit val eqConfigExampleFoo: Eq[ConfigExampleFoo] = Eq.fromUniversalEquals
    val genConfigExampleFoo: Gen[ConfigExampleFoo] = for {
      thisIsAField <- arbitrary[String]
      a            <- arbitrary[Int]
      b            <- arbitrary[Double]
    } yield ConfigExampleFoo(thisIsAField, a, b)
    implicit val arbitraryConfigExampleFoo: Arbitrary[ConfigExampleFoo] = Arbitrary(
      genConfigExampleFoo
    )
  }

  object ConfigExampleBase {
    implicit val eqConfigExampleBase: Eq[ConfigExampleBase] = Eq.fromUniversalEquals
    val genConfigExampleBase: Gen[ConfigExampleBase] =
      Gen.oneOf(Gen.const(ConfigExampleBar), ConfigExampleFoo.genConfigExampleFoo)
    implicit val arbitraryConfigExampleBase: Arbitrary[ConfigExampleBase] = Arbitrary(
      genConfigExampleBase
    )
  }
  val genConfiguration: Gen[Configuration] = for {
    transformMemberNames      <- arbitrary[String => String]
    transformConstructorNames <- arbitrary[String => String]
    useDefaults               <- arbitrary[Boolean]
    discriminator             <- arbitrary[Option[String]]
  } yield Configuration(transformMemberNames, transformConstructorNames, useDefaults, discriminator)

}

class ConfiguredAutoDerivedSuite extends AnyFlatSpec with ScalaCheckPropertyChecks {
  import localExamples._
  implicit val arbitraryConfiguration: Arbitrary[Configuration] = Arbitrary(genConfiguration)
  "Configuration#transformMemberNames" should "support member name transformation using snake_case" in forAll {
    foo: ConfigExampleFoo =>
      implicit val snakeCaseConfig: Configuration = Configuration.default.withSnakeCaseMemberNames

      import foo._
      val json = json"""{ "this_is_a_field": $thisIsAField, "a": $a, "b": $b}"""

      assert(Encoder[ConfigExampleFoo].apply(foo) === json)
      assert(Decoder[ConfigExampleFoo].decodeJson(json) === Right(foo))
  }

  "Configuration#transformMemberNames" should "support member name transformation using kebab-case" in forAll {
    foo: ConfigExampleFoo =>
      implicit val kebabCaseConfig: Configuration = Configuration.default.withKebabCaseMemberNames

      import foo._
      val json = json"""{ "this-is-a-field": $thisIsAField, "a": $a, "b": $b}"""

      assert(Encoder[ConfigExampleFoo].apply(foo) === json)
      assert(Decoder[ConfigExampleFoo].decodeJson(json) === Right(foo))
  }

  "Configuration#useDefaults" should "support using default values during decoding" in {
    forAll { (f: String, b: Double) =>
      implicit val withDefaultsConfig: Configuration = Configuration.default.withDefaults

      val foo: ConfigExampleFoo = ConfigExampleFoo(f, 0, b)
      val json                  = json"""{ "thisIsAField": $f, "b": $b }"""
      val expected              = json"""{ "thisIsAField": $f, "a": 0, "b": $b}"""

      assert(Encoder[ConfigExampleFoo].apply(foo) === expected)
      assert(Decoder[ConfigExampleFoo].decodeJson(json) === Right(foo))
    }
  }

  "Configuration#discriminator" should "support a field indicating constructor" in {
    forAll { foo: ConfigExampleFoo =>
      implicit val withDefaultsConfig: Configuration =
        Configuration.default.withDiscriminator("type")

      import foo._
      val json =
        json"""{ "type": "ConfigExampleFoo", "thisIsAField": $thisIsAField, "a": $a, "b": $b}"""

      assert(Encoder[ConfigExampleBase].apply(foo) === json)
      assert(Decoder[ConfigExampleBase].decodeJson(json) === Right(foo))
    }
  }

  "Configuration#transformConstructorNames" should "support constructor name transformation with snake_case" in forAll {
    foo: ConfigExampleFoo =>
      implicit val snakeCaseConfig: Configuration =
        Configuration.default.withDiscriminator("type").withSnakeCaseConstructorNames

      import foo._
      val json =
        json"""{ "type": "config_example_foo", "thisIsAField": $thisIsAField, "a": $a, "b": $b}"""

      assert(Encoder[ConfigExampleBase].apply(foo) === json)
      assert(Decoder[ConfigExampleBase].decodeJson(json) === Right(foo))
  }

  "Configuration#transformConstructorNames" should "support constructor name transformation with kebab-case" in forAll {
    foo: ConfigExampleFoo =>
      implicit val kebabCaseConfig: Configuration =
        Configuration.default.withDiscriminator("type").withKebabCaseConstructorNames

      import foo._
      val json =
        json"""{ "type": "config-example-foo", "thisIsAField": $thisIsAField, "a": $a, "b": $b}"""

      assert(Encoder[ConfigExampleBase].apply(foo) === json)
      assert(Decoder[ConfigExampleBase].decodeJson(json) === Right(foo))
  }

  "Configuration options" should "work together" in forAll { (f: String, b: Double) =>
    implicit val customConfig: Configuration =
      Configuration.default.withSnakeCaseMemberNames.withDefaults.withDiscriminator("type")

    val foo: ConfigExampleBase = ConfigExampleFoo(f, 0, b)
    val json                   = json"""{ "type": "ConfigExampleFoo", "this_is_a_field": $f, "b": $b}"""
    val expected               = json"""{ "type": "ConfigExampleFoo", "this_is_a_field": $f, "a": 0, "b": $b}"""

    assert(Encoder[ConfigExampleBase].apply(foo) === expected)
    assert(Decoder[ConfigExampleBase].decodeJson(json) === Right(foo))
  }
}
