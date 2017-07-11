package stream_aggregations.aggregation

import stream_aggregations.UnitSpec

class AggregationSpec extends UnitSpec {

  "An aggregation" - {

    val initialValue = 0
    val folding = (a : Int, b: Int) => a + b

    //TODO Investigate why type inference is too weak here.
    val sumAggregation = new Aggregation[Int, Int](initialValue)(folding)

    "when accessing its constituents" - {
      "should return the same initial value" in {
        sumAggregation.initialValue should ===(initialValue)
      }

      "should return the same folding function" in {
        forAll{ (a : Int, b : Int) =>
          sumAggregation.folding(a, b) should ===(folding(a,b))
        }
      }
    }

    "when applied to a sequence" - {
      "should return the initial value if the sequence is empty" in {
        sumAggregation(Seq.empty[Int]) should ===(sumAggregation.initialValue)
      }

      "should return the same as folding over the sequence from the left" in {
        forAll{ someSequence : Seq[Int] =>
          sumAggregation(someSequence) should ===(someSequence.foldLeft(initialValue)(folding))
        }
      }
    }

    "when composed with another aggregation" ignore {
      val productAggregation = new Aggregation[Int, Int](1)(_ * _)

      lazy val tupleAggregation: Aggregation[(Int, Int), (Int, Int)] = ??? //sumAggregation ||| productAggregation

      "should have the tuple of corresponding initial values as its initial value" in {
        tupleAggregation.initialValue should===(sumAggregation.initialValue, productAggregation.initialValue)
      }

      "should apply the folding function element-wise on tuples" in {
        forAll{ (a : Int, b : Int, c : Int, d : Int) =>
          tupleAggregation.folding((a, b), (c,d)) should ===(sumAggregation.folding(a,c), productAggregation.folding(b, d))
        }
      }

      "should fold over sequences component-wise" in {
        forAll{ tuples : Seq[(Int, Int)] =>

          val firstElements = tuples.map(_._1)
          val secondElements = tuples.map(_._2)
          tupleAggregation(tuples) should ===( sumAggregation(firstElements) -> productAggregation(secondElements) )
        }
      }

    }
  }
}
