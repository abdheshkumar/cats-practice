package functor

import cats._
import cats.implicits._
import scala.concurrent.duration._

object InvariantFunctor extends App {

  /**
   * Transform an `F[A]` into an `F[B]` by providing a transformation from `A`
   * to `B` and one from `B` to `A`.
   */
  val durSemigroup: Semigroup[FiniteDuration] =
    Invariant[Semigroup].imap(Semigroup[Long])(Duration.fromNanos)(_.toNanos)
  durSemigroup.combine(2.seconds, 3.seconds)

}
