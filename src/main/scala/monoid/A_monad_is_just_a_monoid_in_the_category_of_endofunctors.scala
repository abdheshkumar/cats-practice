package monoid

object A_monad_is_just_a_monoid_in_the_category_of_endofunctors extends App {

  trait Monoid[T] {
    def zero: T

    def combine(a1: T, a2: T): T
  }

  object IntAddMonoid extends Monoid[Int] {
    def zero: Int = 0

    def combine(a1: Int, a2: Int): Int = a1 + a2
  }

  trait MonoidalCategory {
    type Morphism[F, G]
    type MonoidalProduct[F, G]
    type IdentityObject
  }

  trait CategoryOfSets extends MonoidalCategory {
    type Morphism[F, G]        = F => G //just traditional Function, that translates object of type F into object of type G
    type MonoidalProduct[F, G] = (F, G) // just a way to combine two elements of category, so in this case we are just making a Tuple2 out of them
    type IdentityObject        = Unit
  }

  trait MonoidInCategory[T] {
    type Category <: MonoidalCategory
    def zero: Category#Morphism[Category#IdentityObject, T]
    def combine: Category#Morphism[Category#MonoidalProduct[T, T], T]
  }

  trait MonoidInCategoryOfSets[T] extends MonoidInCategory[T] {
    type Category = CategoryOfSets
    def zero: Unit => T        //zero is function from IdentityObject to T
    def combine: ((T, T)) => T //function from product of T and T to another instance of T
  } //MonoidInCategoryOfSets is almost the same our firs Monoid

  object IntAddMonoidInCategoryOfSets extends MonoidInCategoryOfSets[Int] {
    def zero: Unit => Int            = (a: Unit) => 0
    def combine: ((Int, Int)) => Int = (t: (Int, Int)) => t._1 + t._2
  }
}
