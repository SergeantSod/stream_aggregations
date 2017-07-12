package stream_aggregations.output

import org.scalacheck.{Arbitrary, Gen}
import stream_aggregations.UnitSpec
import Arbitrary.arbitrary

class ShowSpec extends UnitSpec {
  "The Show type class" - {

    "when showing a double" - {

      "should round to 5 decimals" in {
        implicitly[Show[Double]] show 1.123456789 should ===("1.12346")
      }

      "should not truncate large numbers" in {
        implicitly[Show[Double]] show 1111111111111111.123456789 should ===("1111111111111111.1")
      }

      "should not show trailing zeros" in {
        implicitly[Show[Double]] show 0.10000000001 should ===("0.1")
      }
    }

    "when showing an Option" - {
      "shows a Some identical to the value itself" in {
        forAll{ someString:String =>
          implicitly[Show[Option[String]]] show Some(someString) should ===(someString)
        }
      }

      "shows a missing value as question marks" in {
        implicitly[Show[Option[String]]] show None should ===("???")
      }
    }

    "when showing arbitrary Objects" - {
      "shows them identical to their toString-representation" in {

        val otherObjects = Gen.someOf(arbitrary[Int], arbitrary[String], arbitrary[Boolean])

        forAll(otherObjects){ something =>
          implicitly[Show[Any]] show something should ===(something.toString)
        }
      }
    }

  }
}
