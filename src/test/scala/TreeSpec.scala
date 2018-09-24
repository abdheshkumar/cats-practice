import cats.laws.discipline.FunctorTests
import cats.tests.CatsSuite
import functor.TreeFunctor.{Branch, Leaf, Tree}
class TreeSpec extends CatsSuite {

  object TreeArbitrary {
    import org.scalacheck._
    import Arbitrary.arbitrary

    def genLeaf[A: Arbitrary]: Gen[Leaf[A]] = arbitrary[A].map(Leaf(_))

    def genNode[A: Arbitrary](level: Int): Gen[Branch[A]] =
      for {
        left  <- genTree[A](level)
        right <- genTree[A](level)
      } yield Branch(left, right)

    def genTree[A: Arbitrary](level: Int): Gen[Tree[A]] =
      if (level >= 100) {
        genLeaf[A]
      } else Gen.oneOf(Gen.lzy(genLeaf[A]), Gen.lzy(genNode[A](level + 1)))

    implicit def arbTree[A: Arbitrary]: Arbitrary[Tree[A]] =
      Arbitrary(genNode[A](level = 0))

  }
  import TreeArbitrary._
  checkAll("Functor[Tree]", FunctorTests[Tree].functor[Int, Int, Int])
}
