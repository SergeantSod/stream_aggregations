package stream_aggregations.aggregation

class RollingWindowAggregator[A, B, T](aggregation: Aggregation[A, B], windowAggregation: Aggregation[Option[T], B]) {
  def over(aggregationTarget: Iterator[B]): Iterator[A] = {

    aggregationTarget.scanLeft(Vector.empty[(Option[T], A)])(step).map(_.headOption).collect {
      case Some(v) => v._2
    }

  }

  private def step[U](pendingAggregates: Vector[(Option[T], A)], element: B) = {
    pendingAggregates :+ (windowAggregation.initialValue -> aggregation.initialValue) map { case (windowAggregate, aggregate) =>
      windowAggregation.folding(windowAggregate, element) -> aggregate
    } collect { case (windowAggregte@Some(_), aggregate) =>
      windowAggregte -> aggregation.folding(aggregate, element)
    }
  }

}

object RollingWindowAggregator {
  def apply[A, B, T](aggregation: Aggregation[A, B])(windowAggregation: Aggregation[Option[T], B]): RollingWindowAggregator[A, B, T] = new RollingWindowAggregator[A, B, T](aggregation, windowAggregation)
}