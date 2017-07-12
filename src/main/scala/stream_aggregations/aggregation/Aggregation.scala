package stream_aggregations.aggregation

//TODO Explain in documentation what the point is: Captures everything about a fold, and you can either apply it incrementally or apply it like the method call. Your choice on WHAT and how often.

class Aggregation[Aggregate, Element](start: => Aggregate, val folding: (Aggregate, Element) => Aggregate) { leftAggregation =>
  def initialValue: Aggregate = start

  //TODO This is actually pretty annoying, since we want to fiddle in implicits in factory methods, so maybe give this a proper name. See the sum aggregation in default_aggregations
  def apply(target: Traversable[Element]) = target.foldLeft(initialValue)(folding)

  def |||[OtherAggregate](rightAggregation: Aggregation[OtherAggregate, Element])
                         (implicit tc: TupleComposition[Aggregate,OtherAggregate]):
                          Aggregation[tc.R, Element]={

    Aggregation[tc.R, Element](tc.compose(leftAggregation.initialValue, rightAggregation.initialValue)){ (aggregate: tc.R, nextElement: Element) =>
      val (leftAggregate, rightAggregate) = tc.decompose(aggregate)

      val newLeftAggregate = leftAggregation.folding(leftAggregate, nextElement)
      val newRightAggregate = rightAggregation.folding(rightAggregate, nextElement)

      tc.compose(newLeftAggregate, newRightAggregate)
    }

  }

  def of[OtherElement](projection:OtherElement => Element):Aggregation[Aggregate,OtherElement] = {
    Aggregation[Aggregate, OtherElement](start){ (a, otherB) =>
      folding(a, projection(otherB))
    }
  }

}

object Aggregation{
  def apply[A,B](start: =>A)(folding: (A, B) => A):Aggregation[A,B] = new Aggregation[A,B](start, folding)
}
