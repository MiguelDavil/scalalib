package ornicar.scalalib

import scalaz.{ Functor, Monoid }

trait Common {

  implicit final class ornicarFunctor[M[_]: Functor, A](fa: M[A]) {

    def map2[N[_], B, C](f: B ⇒ C)(implicit m: A <:< N[B], f1: Functor[M], f2: Functor[N]): M[N[C]] =
      f1.map(fa) { k ⇒ f2.map(k: N[B])(f) }
  }

  implicit val ornicarBooleanMonoid: Monoid[Boolean] = new Monoid[Boolean] {
    def append(f1: Boolean, f2: ⇒ Boolean) = f1 || f2
    def zero = false
  }

  implicit val ornicarIntMonoid: Monoid[Int] = new Monoid[Int] {
    def append(f1: Int, f2: ⇒ Int) = f1 + f2
    def zero = 0
  }

  implicit val ornicarUnitMonoid: Monoid[Unit] = new Monoid[Unit] {
    def append(f1: Unit, f2: ⇒ Unit) = ()
    def zero = ()
  }

  implicit def ornicarSeqMonoid[A]: Monoid[Seq[A]] = new Monoid[Seq[A]] {
    def append(f1: Seq[A], f2: ⇒ Seq[A]) = f1 ++ f2
    def zero: Seq[A] = Seq.empty
  }

  /**
   * K combinator implementation
   * Provides oneliner side effects
   * See http://hacking-scala.posterous.com/side-effecting-without-braces
   */
  implicit final class ornicarAddKcombinator[A](any: A) {
    def kCombinator(sideEffect: A ⇒ Unit): A = {
      sideEffect(any)
      any
    }
    def ~(sideEffect: A ⇒ Unit): A = kCombinator(sideEffect)
    def pp: A = kCombinator(println)
  }

  implicit final class ornicarRichMap[A, B](m: Map[A, B]) {

    // Add Map.mapKeys, similar to Map.mapValues
    def mapKeys[C](f: A ⇒ C): Map[C, B] = m map {
      case (a, b) ⇒ (f(a), b)
    } toMap

    // Add Map.filterValues, similar to Map.filterKeys
    def filterValues(p: B ⇒ Boolean): Map[A, B] = m filter { x ⇒ p(x._2) }
  }

  implicit final class ornicarRichIdentity[A](a: A) {

    def combine[B](o: Option[B])(f: (A, B) ⇒ A): A = o match {
      case None    ⇒ a
      case Some(b) ⇒ f(a, b)
    }
  }
}