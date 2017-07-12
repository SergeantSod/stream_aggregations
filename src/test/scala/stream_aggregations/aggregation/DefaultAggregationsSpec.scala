package stream_aggregations.aggregation

import default_aggregations._
import org.scalacheck.{Arbitrary, Gen}
import stream_aggregations.UnitSpec

class DefaultAggregationsSpec extends UnitSpec {

  def nonEmptySeqs[T : Arbitrary]: Gen[Seq[T]] = Arbitrary.arbitrary[Seq[T]] suchThat { seq => !(seq.isEmpty) }

  "Predefined default aggregations" - {
    "sum" - {
      "should sum up Ints" in {
        forAll{ someInts: Seq[Int] =>
          sum[Int].apply(someInts) should ===(someInts.sum)
        }
      }

      "should sum up Doubles" in {
        forAll { someDoubles: Seq[Double] =>
          sum[Double].apply(someDoubles) should ===(someDoubles.sum)
        }
      }
    }

    "count" - {
      "should count elements" in {
        forAll{ booleans: Seq[Boolean] =>
          count.apply(booleans) should ===(booleans.size)
        }
      }
    }

    "last" - {
      "should pick the last element wrapped in an option for non-empty sequences" in {
        forAll(nonEmptySeqs[Int]){ someInts =>
          last[Int].apply(someInts) should ===(Some(someInts.last))
        }
      }

      "should not lose nulls in the last position" in {
        last[String].apply(Seq[String](null)) should !==(None)
      }

      "should return None for an empty sequence" in {
        last[String].apply(Seq()) should ===(None)
      }
    }

    "min" - {
      "should pick the minimum element wrapped in an option for non-empty sequences" in {
        forAll(nonEmptySeqs[Int]){ someInts =>
          min[Int].apply(someInts) should ===(Some(someInts.min))
        }
      }

      "should return None for an empty sequence" in {
        min[Int].apply(Seq()) should ===(None)
      }
    }

    "max" - {
      "should pick the maximum element wrapped in an option for non-empty sequences" in {
        forAll(nonEmptySeqs[Int]){ someInts =>
          max[Int].apply(someInts) should ===(Some(someInts.max))
        }
      }

      "should return None for an empty sequence" in {
        max[Int].apply(Seq()) should ===(None)
      }
    }
  }



}
