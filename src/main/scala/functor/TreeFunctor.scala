package functor
import cats.Functor
import cats.kernel.Eq

object TreeFunctor {
  sealed trait Tree[+A]

  final case class Branch[A](left: Tree[A], right: Tree[A]) extends Tree[A]

  final case class Leaf[A](value: A) extends Tree[A]

  object Tree {

    implicit def functor: Functor[Tree] = new Functor[Tree] {

      def map[A, B](tree: Tree[A])(func: A => B): Tree[B] = tree match {

        case Leaf(value) => Leaf(func(value))

        case Branch(left, right) => Branch(map(left)(func), map(right)(func))
      }
    }

    implicit def eq[A: Eq]: Eq[Tree[A]] = {

      Eq.instance[Tree[A]] { (tree1: Tree[A], tree2: Tree[A]) =>
        (tree1, tree2) match {

          case (Branch(left1, right1), Branch(left2, right2)) => left1 == left2 && right1 == right2

          case (Leaf(value1), Leaf(value2)) => value1 == value2

          case _ => false

        }

      }
    }
  }
}
