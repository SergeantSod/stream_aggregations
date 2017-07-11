package stream_aggregations.aggregation

//TODO Explain in documentation what the point is: Captures everything about a fold, and you can either apply it incrementally or apply it like the method call. Your choice on WHAT and how often.
//TODO Consider Variance annotation
class Aggregation[A, B](start: => A)(val folding: (A, B) => A) {
  def initialValue: A = start

  def apply(target: Traversable[B]) = target.foldLeft(initialValue)(folding)
}
