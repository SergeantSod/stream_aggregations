package stream_aggregations.output

import stream_aggregations.tuples.TupleComposition

/**
  * A type class for something that can be shown as a record in a table.
  * Each such object gets its own line in the table, and each part of it will get its own column within that line.
  * @tparam T
  */
trait ShowParts[T]{
  def showParts(showable: T): Seq[String]
}

trait LowPriorityShowPartImplicits {
  implicit def showSinglePart[T](implicit s: Show[T]):ShowParts[T] = new ShowParts[T] {
    override def showParts(showable: T): Seq[String] = Vector(s show showable)
  }
}

object ShowParts extends LowPriorityShowPartImplicits {

  implicit def showPartsOfSeq[T](implicit elementShow: Show[T]):ShowParts[Seq[T]]= new ShowParts[Seq[T]] {
    override def showParts(showable: Seq[T]): Seq[String] = showable.map(elementShow.show).toSeq
  }

  implicit def showPartsOfTuple[A,B,R](implicit tc: TupleComposition.Aux[A,B,R], s: Show[B] , sp: ShowParts[A]):ShowParts[R] = new ShowParts[R]{
    override def showParts(showable: R): Seq[String] = {
      val (first, second) = tc.decompose(showable)
      sp.showParts(first) :+ s.show(second)
    }
  }
}