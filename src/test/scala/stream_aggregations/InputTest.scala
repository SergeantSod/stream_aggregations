package stream_aggregations

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.{FreeSpec, Matchers, WordSpec}

class InputTest extends FreeSpec with Matchers with TypeCheckedTripleEquals with TempFileSpec {
  "The input package" - {

     //TODO Consider using a prop spec instead of hard-coding some example values here. Not sure how slow that is if we actually create temp files
     val firstLine = "some content for the first line"
     val secondLine = "some different content for the second line"

     val fileWithContent = createTempFileWithText(s"$firstLine\n$secondLine")

      "should open files and return the lines" in {
        input.openFile(fileWithContent.getCanonicalPath).toList should ===(List(firstLine, secondLine))
      }
  }
}
