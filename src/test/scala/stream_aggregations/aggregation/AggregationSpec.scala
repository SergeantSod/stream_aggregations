package stream_aggregations.aggregation

import stream_aggregations.UnitSpec

class AggregationSpec extends UnitSpec {

  "An aggregation" - {

    val initialValue = 0
    val folding = (a : Int, b: Int) => a + b

    lazy val sumAggregation = Aggregation(initialValue)(folding)

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

    "when projected to a component" - {

      lazy val sumOfFirstComponent = sumAggregation.on{ pair:(Int, Int) => pair._1 }

      "should have the same initial value" - {
        sumOfFirstComponent.initialValue should ===(sumAggregation.initialValue)
      }

      "should return a folding function that works on the component" in {
        forAll{ (a : Int, b : (Int, Int)) =>
          sumOfFirstComponent.folding(a, b) should ===(folding(a,b._1))
        }
      }

      "should return the same as folding over the components of each element of the sequence from the left" in {
        forAll{ someTuples : Seq[(Int, Int)] =>
          val firstComponents = someTuples.map(_._1)
          sumOfFirstComponent(someTuples) should ===(sumAggregation(firstComponents))
        }
      }


    }

    "when composed with another aggregation" - {

      val productAggregation = Aggregation(1){(_:Int) * (_:Int)}

      lazy val tupleAggregation = sumAggregation ||| productAggregation

      "should have the tuple of corresponding initial values as its initial value" in {
        tupleAggregation.initialValue should===(sumAggregation.initialValue, productAggregation.initialValue)
      }

      "should apply the folding function element-wise on tuples" in {
        forAll{ (a : Int, b : Int, c : Int) =>
          tupleAggregation.folding((a, b), c) should ===(sumAggregation.folding(a,c), productAggregation.folding(b, c))
        }
      }

      "should fold over sequences component-wise" in {
        forAll{ elements : Seq[Int] =>
          tupleAggregation(elements) should ===( sumAggregation(elements) -> productAggregation(elements) )
        }
      }

    }
  }
}
