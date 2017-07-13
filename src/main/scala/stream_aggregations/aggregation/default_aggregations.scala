package stream_aggregations.aggregation

object default_aggregations {
  /**
    * An aggregation that results in the sum of elements
    * @param numeric
    * @tparam T
    * @return
    */
  def sum[T](implicit numeric: Numeric[T]):Aggregation[T,T] = Aggregation(numeric.zero)(numeric.plus)

  /**
    * An aggregation tha results in the last element aggregated over.
    * @tparam T
    * @return
    */
  def last[T]:Aggregation[Option[T],T] = optionalBinop[T]{ (_, next) => Some(next) }

  /**
    * An aggregation that couns the elements aggregated over.
    * @tparam T
    * @return
    */
  def count[T]:Aggregation[Int, T] = Aggregation(0){ (count, _) => count + 1 }

  /**
    * An aggregation that results in the (optional) minimum element that was aggregated over.
    * @param ordering
    * @tparam T
    * @return
    */
  def min[T](implicit ordering: Ordering[T]):Aggregation[Option[T],T] = optionalBinop[T]{ (a,b) => Some(ordering.min(a,b)) }

  /**
    * An aggregation that results in the (optional) maximum element that was aggregated over.
    * @param ordering
    * @tparam T
    * @return
    */
  def max[T](implicit ordering: Ordering[T]):Aggregation[Option[T],T] = optionalBinop[T]{ (a,b) => Some(ordering.max(a,b)) }

  /**
    * Helper method to construct aggregations that operate on elements only. If there are no elements, None is returned.
    * @param binop
    * @tparam T
    * @return
    */
  def optionalBinop[T](binop:(T,T) => Option[T]) = Aggregation[Option[T], T](None){
    case(None, next) => Some(next)
    case(Some(current), next) => binop(current, next)
  }
}
