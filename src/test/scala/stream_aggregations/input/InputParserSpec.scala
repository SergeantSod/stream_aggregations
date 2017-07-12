package stream_aggregations.input

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.Gen
import stream_aggregations.UnitSpec

class InputParserSpec extends UnitSpec {
  "The Input parser" - {

    "should reconstitute valid inputs from their string representations" in {

      val whitespace = Gen.nonEmptyListOf(Gen.oneOf(Gen.const(' '), Gen.const('\t'))).map(_.mkString)

      forAll(arbitrary[Int], arbitrary[Double], whitespace) { (intValue, doubleValue, someWhiteSpace) =>

        InputParser.parse(s"$intValue${someWhiteSpace}$doubleValue") should ===(intValue, doubleValue)
      }

    }

    "should throw an exception if the outer extractor in the mapping function does not match" in {
      a[ParseException] should be thrownBy {
        InputParser.parse("123;123")
      }
    }

    "should throw an exception if a nested extractor in the mapping function fails" in {
      a[ParseException] should be thrownBy {
        InputParser.parse("foo  12.13")
      }
    }
  }

}
