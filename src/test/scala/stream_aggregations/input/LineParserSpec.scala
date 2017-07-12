package stream_aggregations.input

import org.scalacheck.{Arbitrary, Gen}
import stream_aggregations.UnitSpec
import stream_aggregations.input.LineParser.{InputParser, Raw}
import Arbitrary.arbitrary
import stream_aggregations.input.extractors._

class LineParsersSpec extends UnitSpec {
  "line parsers" - {

    "when using the raw parser" - {
      "should return the line unchanged" in {
        forAll{ line:String =>
          Raw.parse(line) should ===(line)
        }

      }
    }

    "when parsing input tuples" - {

      "should reconstitute valid inputs from their string representations" in {

        val whitespace = Gen.nonEmptyListOf(Gen.oneOf(Gen.const(' '), Gen.const('\t'))).map(_.mkString)

        forAll(arbitrary[Int], arbitrary[Double], whitespace){ (intValue, doubleValue, someWhiteSpace) =>

          InputParser.parse(s"$intValue${someWhiteSpace}$doubleValue") should===(intValue, doubleValue)
        }

      }

      "should throw an exception if the outer extractor in the mapping function does not match" in {
        a[ParseException] should be thrownBy{
          InputParser.parse("123;123")
        }
      }

      "should throw an exception if a nested extractor in the mapping function fails" in {
        a[ParseException] should be thrownBy{
          InputParser.parse("foo  12.13")
        }
      }
    }
  }
}
