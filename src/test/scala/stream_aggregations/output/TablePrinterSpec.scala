package stream_aggregations.output

import java.io.PrintStream

import stream_aggregations.{StringNormalization, UnitSpec}

class TablePrinterSpec extends UnitSpec with StringNormalization {
  "A table printer" - {

    type Line = (Int, String, Double)
    lazy val tablePrinter = TablePrinter[Line](Seq("int", "string", "double"))

    lazy val exampleElements: Vector[Line] = {
      Vector(
        (0, "one", 2.0),
        (1, "two", 1.5),
        (3, "three", 0.5)
      )
    }

    def printed(elements: Traversable[Line]): String = {
      val stream = new java.io.ByteArrayOutputStream()
      tablePrinter.printTo(new PrintStream(stream))(elements.toIterator)
      stream.toString()
    }

    "should print sequences of elements" in {
      (
        printed(exampleElements) should === {
          """|int string double
             |------------------
             |0   one    2
             |1   two    1.5
             |3   three  0.5
             |""".stripMargin
        }
        ) (after being trimmedPerLine)
    }


    "should pad header if it is narrower" in {

      val lineWithWideElement = exampleElements(1).copy(_2 = "wiiiiiiiiiide")
      val wideExampleElements = exampleElements.updated(1, lineWithWideElement)
      (
        printed(wideExampleElements) should === {
          """|int string        double
             |-------------------------
             |0   one           2
             |1   wiiiiiiiiiide 1.5
             |3   three         0.5
             |""".stripMargin
        }
      ) (after being trimmedPerLine)
    }

  }
}
