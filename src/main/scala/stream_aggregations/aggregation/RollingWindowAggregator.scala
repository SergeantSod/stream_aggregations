package stream_aggregations.aggregation

class RollingWindowAggregator[A,B,T](aggregation: Aggregation[A,B], windowAggregation: Aggregation[Option[T],B]) {
  def over(aggregationTarget: Traversable[B]): Traversable[A] = new Traversable[A]{
    override def foreach[U](emit: (A) => U): Unit = {

      var pendingAggregates = Vector.empty[(Option[T], A)]

      aggregationTarget.foreach{ element =>

        pendingAggregates = pendingAggregates :+ windowAggregation.initialValue -> aggregation.initialValue

        pendingAggregates = pendingAggregates map { case(windowAggregate, aggregate) =>
          //TODO See how to DRY this up with the iteration below
          windowAggregation.folding(windowAggregate, element) -> aggregate
        } collect { case (windowAggregte @ Some(_), aggregate) =>
          windowAggregte -> aggregation.folding(aggregate, element)
        }

        pendingAggregates.headOption.foreach{ a => emit(a._2) }
      }
    }
  }
}

object RollingWindowAggregator {
  def apply[A,B,T](aggregation: Aggregation[A,B])(windowAggregation: Aggregation[Option[T],B]):RollingWindowAggregator[A,B,T] = new RollingWindowAggregator[A,B,T](aggregation, windowAggregation)
}