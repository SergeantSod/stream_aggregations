package stream_aggregations.tuples

/**
  * A type-class to allow working with tuples of arbitrary arity (currently works up to an arity of 6).
  * @tparam A
  * @tparam B
  */
sealed trait TupleComposition[A, B] {

  type R

  def compose(a: A, b: B): R

  def decompose(r: R): (A, B)
}

object TupleComposition extends Implicits {

  trait Aux[A, B, C] extends TupleComposition[A, B] {
    type R = C
  }

}

trait LowPriorityImplicits {
  implicit def pairComposition[A, B]: TupleComposition.Aux[A, B, (A, B)] = new TupleComposition.Aux[A, B, (A, B)] {
    override def compose(a: A, b: B): R = (a, b)

    override def decompose(r: (A, B)): (A, B) = r
  }
}

trait Implicits extends LowPriorityImplicits {
  implicit def tuple3Composition[A, B, C]: TupleComposition.Aux[(A, B), C, (A, B, C)] = new TupleComposition.Aux[(A, B), C, (A, B, C)] {

    override def compose(head: (A, B), c: C): R = (head._1, head._2, c)

    override def decompose(r: (A, B, C)): ((A, B), C) = ((r._1, r._2), r._3)
  }

  implicit def tuple4Composition[A, B, C, D]: TupleComposition.Aux[(A, B, C), D, (A, B, C, D)] = new TupleComposition.Aux[(A, B, C), D, (A, B, C, D)] {
    override def compose(head: (A, B, C), d: D): R = (head._1, head._2, head._3, d)

    override def decompose(r: (A, B, C, D)): ((A, B, C), D) = ((r._1, r._2, r._3), r._4)
  }

  implicit def tuple5Composition[A, B, C, D, E]: TupleComposition.Aux[(A, B, C, D), E, (A, B, C, D, E)] = new TupleComposition.Aux[(A, B, C, D), E, (A, B, C, D, E)] {
    override def compose(head: (A, B, C, D), e: E): R = (head._1, head._2, head._3, head._4, e)

    override def decompose(r: (A, B, C, D, E)): ((A, B, C, D), E) = ((r._1, r._2, r._3, r._4), r._5)
  }

  implicit def tuple6Composition[A, B, C, D, E, F]: TupleComposition.Aux[(A, B, C, D, E), F, (A, B, C, D, E, F)] = new TupleComposition.Aux[(A, B, C, D, E), F, (A, B, C, D, E, F)] {
    override def compose(head: (A, B, C, D, E), f: F): R = (head._1, head._2, head._3, head._4, head._5, f)

    override def decompose(r: R): ((A, B, C, D, E), F) = ((r._1, r._2, r._3, r._4, r._5), r._6)
  }
}
