package stream_aggregations.aggregation

/**
  * Applies an aggregation over some rolling windows of a sequence of elements.
  * The boundaries of the rolling windows are determined on an element-wise basis by a so-called window aggregation.
  * I.e. the primary aggregation will be run on each element and a number of of its preceding elements
  * depending on the result of the window aggregation. The window aggregation can carry state in an [[Option]] and later use it to decide
  * that some rolling window is no longer needed for future elements. In this case, the window aggregation returns None.
  * In fact, care must be taken to return None after some elements, otherwise excessive memory will be used for the
  * internal buffer of pending rolling windows.
  *
  * @param aggregation The primary aggregation to calculate over each rolling window.
  * @param windowAggregation The window aggregation
  * @tparam Aggregate The aggregate type of the primary aggregation
  * @tparam Element The element type of the primary aggregation.
  * @tparam W The intermediate type of the window aggregation.
  */
class RollingWindowAggregator[Aggregate, Element, W](aggregation: Aggregation[Aggregate, Element], windowAggregation: Aggregation[Option[W], Element]) {

  /**
    * Given an iterator of elements, return an iterator with the results of running the primary aggregation over the rolling windows.
    *
    * @param aggregationTarget An iterator of elements to be aggregated over
    * @return The iterater of the aggregation results
    */
  def over(aggregationTarget: Iterator[Element]): Iterator[Aggregate] = {

    aggregationTarget.scanLeft(Vector.empty[(Option[W], Aggregate)])(step).map(_.headOption).collect {
      case Some(v) => v._2
    }

  }

  private def step[U](pendingAggregates: Vector[(Option[W], Aggregate)], element: Element) = {
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