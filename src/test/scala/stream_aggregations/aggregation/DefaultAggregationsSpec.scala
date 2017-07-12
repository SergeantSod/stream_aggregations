package stream_aggregations.aggregation

import default_aggregations._
import org.scalacheck.Arbitrary
import stream_aggregations.UnitSpec

class DefaultAggregationsSpec extends UnitSpec {
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
        val nonEmptyVectors = Arbitrary.arbitrary[Vector[Int]] suchThat {vector => !(vector.isEmpty)}

        forAll(nonEmptyVectors){ someInts =>
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
  }
}
