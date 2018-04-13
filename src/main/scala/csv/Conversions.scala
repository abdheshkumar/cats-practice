package csv

import scala.util.Try

object Conversions {

  import cats.implicits._
  import cats.data.ValidatedNel
  import shapeless._, labelled._

  private type Result[A] = ValidatedNel[ParseFailure, A]

  case class ParseFailure(error: String)

  trait Convert[V] {
    def parse(input: String): Result[V]
  }

  object Convert {
    def to[V](input: String)(implicit C: Convert[V]): Result[V] =
      C.parse(input)

    def instance[V](body: String => Result[V]): Convert[V] = new Convert[V] {
      def parse(input: String): Result[V] = body(input)
    }

    implicit def booleans: Convert[Boolean] =
      Convert.instance(
        s =>
          Try(s.toBoolean)
            .toEither
            .leftMap(e => ParseFailure(s"Not a Boolean ${e.getMessage}"))
            .toValidatedNel)

    implicit def ints: Convert[Int] =
      Convert.instance(
        s =>
          Try(s.toInt)
            .toEither
            .leftMap(e => ParseFailure(s"Not an Int ${e.getMessage}"))
            .toValidatedNel)

    implicit def strings: Convert[String] = Convert.instance(s => s.validNel)
  }

  sealed trait Schema[A] {
    def readFrom(input: Map[String, String]): ValidatedNel[ParseFailure, A]
  }

  object Schema {
    def of[A](implicit s: Schema[A]): Schema[A] = s

    private def instance[A](body: Map[String, String] => Result[A]): Schema[A] = new Schema[A] {
      def readFrom(input: Map[String, String]): Result[A] = body(input)
    }

    implicit val noOp: Schema[HNil] = Schema.instance(_ => HNil.validNel)

    implicit def parsing[K <: Symbol, V: Convert, T <: HList](
                                                               implicit key: Witness.Aux[K],
                                                               next: Schema[T]): Schema[FieldType[K, V] :: T] =
      Schema.instance { input =>
        val fieldName = key.value.name
        val parsedField = input
          .get(fieldName)
          .fold(ParseFailure(s"$fieldName is missing").invalidNel[V])(entry => Convert.to[V](entry))
          .map(f => field[K](f))

        (parsedField, next.readFrom(input)).mapN(_ :: _)
      }

    implicit def classes[A, R <: HList](
                                         implicit repr: LabelledGeneric.Aux[A, R],
                                         schema: Schema[R]): Schema[A] =
      Schema.instance { input =>
        schema.readFrom(input).map(x => repr.from(x))
      }
  }

}

trait MyTypeClass[V] {
  def encode(v: V): String

  def decode(s: String): V
}

object MyTypeClass {
  def instance[E](en: E => String,
                     de: String => E): MyTypeClass[E] = new MyTypeClass[E] {
    override def encode(v: E): String = en(v)
    override def decode(s: String): E = de(s)
  }
}