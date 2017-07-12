package stream_aggregations.aggregation

object default_aggregations {
  def sum[T](implicit numeric: Numeric[T]):Aggregation[T,T] = Aggregation(numeric.zero)(numeric.plus)

  def last[T]:Aggregation[Option[T],T] = Aggregation(None:Option[T]){ (_, next) => Some(next) }

  def count:Aggregation[Int, Any] = Aggregation(0){ (count, _) => count + 1 }
}
