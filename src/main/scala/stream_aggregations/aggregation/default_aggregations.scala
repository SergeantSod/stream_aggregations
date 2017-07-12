package stream_aggregations.aggregation

object default_aggregations {
  def sum[T](implicit numeric: Numeric[T]):Aggregation[T,T] = Aggregation(numeric.zero)(numeric.plus)

  def last[T]:Aggregation[Option[T],T] = Aggregation(None:Option[T]){ (_, next) => Some(next) }

  def count[T]:Aggregation[Int, T] = Aggregation(0){ (count, _) => count + 1 }

  def min[T](implicit ordering: Ordering[T]):Aggregation[Option[T],T] = optionalBinop[T]{ (a,b) => Some(ordering.min(a,b)) }

  def max[T](implicit ordering: Ordering[T]):Aggregation[Option[T],T] = optionalBinop[T]{ (a,b) => Some(ordering.max(a,b)) }

  def optionalBinop[T](binop:(T,T) => Option[T]) = Aggregation[Option[T], T](None){
    case(None, next) => Some(next)
    case(Some(current), next) => binop(current, next)
  }
}
