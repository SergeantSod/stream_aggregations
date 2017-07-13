package stream_aggregations.aggregation

import stream_aggregations.UnitSpec
import default_aggregations._

class RollingWindowAggregatorSpec extends UnitSpec {
  "A rolling window aggregation" - {
    lazy val upToThreeElements = Aggregation(Option(0)){ (count, _:Int) =>
      count map { _ + 1} filter { _ <= 3 }
    }

    lazy val rollingWindowAggregation = RollingWindowAggregator(sum[Int] ||| last[Int])(upToThreeElements)

    "should aggregate over rolling windows" in {
      forAll{ sequenceOfInts: Seq[Int] =>
        (rollingWindowAggregation over sequenceOfInts.iterator).toList should ==={
          sequenceOfInts.indices.map{ index =>
            val window = sequenceOfInts.slice(index-2, index+1)
            window.sum -> window.lastOption
          }.toList
        }
      }
    }

    "should produce no aggregates from an empty sequence" in {
      (rollingWindowAggregation over Iterator.empty) should be(empty)
    }

  }
}
