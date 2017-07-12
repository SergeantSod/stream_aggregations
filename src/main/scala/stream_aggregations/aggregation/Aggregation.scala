package stream_aggregations.aggregation

//TODO Explain in documentation what the point is: Captures everything about a fold, and you can either apply it incrementally or apply it like the method call. Your choice on WHAT and how often.
//TODO Consider Variance annotation
//Rename Type variables to something that is still understandable in two weeks.
class Aggregation[A, B](start: => A, val folding: (A, B) => A) { leftAggregation =>
  def initialValue: A = start

  //TODO This is actually pretty annoying, since we want to fiddle in implicits in factory methods, so maybe give this a proper name. See the sum aggregation in default_aggregations
  def apply(target: Traversable[B]) = target.foldLeft(initialValue)(folding)

  def |||[OtherA](rightAggregation: Aggregation[OtherA, B])(implicit tc: TupleComposition[A,OtherA]): Aggregation[tc.R, B]={
    Aggregation[tc.R, B](tc.compose(leftAggregation.initialValue, rightAggregation.initialValue)){ (aggregate: tc.R, nextElement: B) =>
      val (leftAggregate, rightAggregate) = tc.decompose(aggregate)

      val newLeftAggregate = leftAggregation.folding(leftAggregate, nextElement)
      val newRightAggregate = rightAggregation.folding(rightAggregate, nextElement)

      tc.compose(newLeftAggregate, newRightAggregate)
    }
  }

  def on[OtherB](projection:OtherB => B):Aggregation[A,OtherB] = Aggregation[A, OtherB](start){ (a: A, otherB: OtherB) =>
    folding(a, projection(otherB))
  }

}

object Aggregation{
  def apply[A,B](start: =>A)(folding: (A, B) => A):Aggregation[A,B] = new Aggregation[A,B](start, folding)
}
