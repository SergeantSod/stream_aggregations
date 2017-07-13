package stream_aggregations.aggregation

import stream_aggregations.tuples.TupleComposition

/**
  * An aggregation consumes a series of values and combines each of them with an initial value via a binary operation.
  * This is the same pattern as:
  * {{{
  *   val someTraversable : Traversable[T] = ???
  *   someTraversable.foldLeft(start)(folding)
  * }}}
  *
  * An instance of Aggregation captures all the necessary aspects of the foldLeft, i.e. both the initial value and the binary operation.
  * Therefore, it models an aggregation without making assumptions about what (or precisely how, or how often) it is applied to. This is up to the context. See [[stream_aggregations.aggregation.RollingWindowAggregator]].
  *
  * @param start The initial value
  * @param folding The binary operation
  * @tparam Aggregate The type of the aggregate (i.e. result) that will be returned at the end
  * @tparam Element The type of the consumed elements
  */

class Aggregation[Aggregate, Element](start: => Aggregate, val folding: (Aggregate, Element) => Aggregate) { leftAggregation =>
  def initialValue: Aggregate = start

  /**
    * Evaluates the aggregation by folding it over a container of elements.
    * @param target The elements to be aggregated
    * @return The aggregate
    */
  def over(target: Traversable[Element]) = target.foldLeft(initialValue)(folding)

  /**
    * Laterally composes this aggregation with another one that operates on the same element type.
    * While the resulting aggregation will consume the same element type, it will produce an aggregate type that is a
    * tuple of the corresponding original aggregations' aggregates.
    *
    * @param rightAggregation The other aggregation that will produce a component of the resulting tuple
    * @param tc
    * @tparam OtherAggregate The right aggregation's result type
    * @return The laterally composed aggregation
    */
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

  /**
    * Composes a projection from a different element type in front of this aggregation's binary operation. The resulting
    * aggregation will consume that element type by feeding values through the projection before applying the binary
    * operation.
    *
    * @param projection A projection from OtherElement to Element
    * @tparam OtherElement The new element type
    * @return An aggregation that consumes instances of OtherElement
    */
  def of[OtherElement](projection:OtherElement => Element):Aggregation[Aggregate,OtherElement] = {
    Aggregation[Aggregate, OtherElement](start){ (a, otherB) =>
      folding(a, projection(otherB))
    }
  }

}

object Aggregation{
  def apply[A,B](start: =>A)(folding: (A, B) => A):Aggregation[A,B] = new Aggregation[A,B](start, folding)
}
