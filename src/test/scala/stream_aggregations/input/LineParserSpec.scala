package stream_aggregations

import input._
import stream_aggregations.input.extractors._

class LineParsersSpec extends UnitSpec {
  "A separated-value line parser" - {

    lazy val lineParser = new SeparatedValueParser("\\s*;\\s*")

    "when parsing a line" - {
      "should return an array of the separated values" in {
        lineParser.parse("first;  second  ;third") should ===(Array("first", "second", "third"))
      }
    }

    "when mapped to a different type via extractors" - {
      lazy val tupleParser = lineParser map { case Array(AnInt(i), ADouble(d)) => (i,d) }

      "should return a value of the right type that result from the mapping" in {
        tupleParser.parse("123  ; 12.13") should===(123, 12.13)
      }

      "should throw an exception if the outer extractor in the mapping function does not match" in {
        a[ParseException] should be thrownBy{
          tupleParser.parse("123  ; 12.13; 123")
        }
      }

      "should throw an exception if a nested extractor in the mapping function fails" in {
        a[ParseException] should be thrownBy{
          tupleParser.parse("foo  ; 12.13")
        }
      }
    }
  }
}
