package stream_aggregations.output

import stream_aggregations.UnitSpec

class TablePrinterSpec extends UnitSpec {
  "A table printer" - {
    lazy val tablePrinter = TablePrinter[(Int, String, Double)](Seq("intHeader", "stringHeader", "doubleHeader"))

    "should print sequences of elements" in pending

    "when aligning columns" - {
      "should pad column values if it is more narrow" in pending
      "should pad header values if it is more narrow" in pending
    }
  }
}
