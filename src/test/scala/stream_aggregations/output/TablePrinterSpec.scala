package stream_aggregations.output

import stream_aggregations.UnitSpec

class TablePrinterSpec extends UnitSpec {
  "A table printer" - {
    lazy val tablePrinter = TablePrinter[(Int, String, Double)](Seq("intHeader", "stringHeader", "doubleHeader"))
  }
}
