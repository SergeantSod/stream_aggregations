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
  }

}
