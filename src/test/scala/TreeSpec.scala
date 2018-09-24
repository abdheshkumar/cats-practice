import cats.laws.discipline.FunctorTests
import cats.tests.CatsSuite
import functor.TreeFunctor.{Branch, Leaf, Tree}
class TreeSpec extends CatsSuite {

  object TreeArbitrary {
    import org.scalacheck._
    import Arbitrary.arbitrary

    def genLeaf[A: Arbitrary]: Gen[Leaf[A]] = arbitrary[A].map(Leaf(_))

    def genNode[A: Arbitrary]: Gen[Branch[A]] =
      for {
        left  <- genTree[A]
        right <- genTree[A]
      } yield Branch(left, right)

    def genTree[A: Arbitrary]: Gen[Tree[A]] = Gen.oneOf(Gen.lzy(genLeaf[A]), Gen.lzy(genNode[A]))

    implicit def arbTree[A: Arbitrary]: Arbitrary[Tree[A]] =
      Arbitrary(genNode[A])

  }
  import TreeArbitrary._
  checkAll("Functor[Tree]", FunctorTests[Tree].functor[Int, Int, Int])
}
