package stream_aggregations.aggregation

class RollingWindowAggregator[A,B,T](aggregation: Aggregation[A,B], windowAggregation: Aggregation[Option[T],B]) {
  def over(aggregationTarget: Traversable[B]): Traversable[A] = new Traversable[A]{
    override def foreach[U](emit: (A) => U): Unit = {

      var pendingAggregates = Vector.empty[(Option[T], A)]

      aggregationTarget.foreach{ element =>
        pendingAggregates = pendingAggregates :+ windowAggregation.initialValue -> aggregation.initialValue

        val(emittees, remaining) = pendingAggregates map { case(windowAggregate, aggregate) =>
          //TODO See how to DRY this up with the iteration below
          windowAggregation.folding(windowAggregate, element) -> aggregate
        } partition {
          case(None, _) => true
          case        _ => false
        }

        emittees.foreach{ x => emit(x._2) }
        pendingAggregates = remaining.map{ case(windowAggregate, aggregate) =>
          //TODO See how to DRY this up with the iteration above
          windowAggregate -> aggregation.folding(aggregate, element)
        }
      }

      pendingAggregates.map(_._2).foreach(emit)
    }
  }
}

object RollingWindowAggregator {
  def apply[A,B,T](aggregation: Aggregation[A,B])(windowAggregation: Aggregation[Option[T],B]):RollingWindowAggregator[A,B,T] = new RollingWindowAggregator[A,B,T](aggregation, windowAggregation)
}