package stream_aggregations.input

import stream_aggregations.UnitSpec
import stream_aggregations.input.LineParser.Raw

class RawParserSpec extends UnitSpec {

  "The Raw Parser" - {
    "should return the line unchanged" in {
      forAll { line: String =>
        Raw.parse(line) should ===(line)
      }
    }

    "when mapped to a different data type via a partial function" - {

      val booleanParser: LineParser[Boolean] = Raw.map{
        case "true" => true
        case "false" => false
      }

      "should parse well-formed strings" in {
        booleanParser.parse("true") should ===(true)
        booleanParser.parse("false") should ===(false)
      }

      "should throw an exception for malformed strings" in {
        a[ParseException] should be thrownBy{
          booleanParser.parse("something funky")
        }
      }

    }
  }

}
