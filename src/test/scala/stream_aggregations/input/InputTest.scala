package stream_aggregations.input

import stream_aggregations.{TempFileSpec, UnitSpec, input}

class InputTest extends UnitSpec with TempFileSpec {

  "The input package" - {

    "when reading from a file" - {

      val firstLine = "some content for the first line"
      val secondLine = "some different content for the second line"

      val fileWithContent = createTempFileWithText(s"$firstLine\n$secondLine")

      "should pass the lines to a lambda" in {
        input.readingFrom(fileWithContent.getCanonicalPath){ lines =>
          lines.toList should ===(List(firstLine, secondLine))
        }
      }

      "should pass control to a lambda" in {
        var called = false

        input.readingFrom(fileWithContent.getCanonicalPath){ _ => called = true }
        called should be(true)
      }
    }



  }

}
