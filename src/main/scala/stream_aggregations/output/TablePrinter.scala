package stream_aggregations.output

import java.io.PrintStream

import scala.math.max


case class TablePrinter[T](headers: Seq[String], headerSeparator:Char='-', columnPadding:Char=' ', columnWidthProbingDepth:Int=25)(implicit showParts: ShowParts[T]){
  def printTo(outputStream: PrintStream)(target: Iterator[T]): Unit = {
    val forProbing = target.take(columnWidthProbingDepth).toList
    val columnWidths = probeColumnWidths(forProbing)
    val tableWidth = columnWidths.sum + columnWidths.size

    printWithColumnWidths(outputStream, columnWidths)(headers)
    outputStream.println("".padTo(tableWidth, headerSeparator))

    forProbing foreach printWithColumnWidths(outputStream, columnWidths)
    target foreach printWithColumnWidths(outputStream, columnWidths)
  }

  def probeColumnWidths(elements: Traversable[T]): Seq[Int] = {
    val headerWidths = headers.map(_.length)
    val elementWidths = elements.map(showParts.showParts).map{ _.map(_.length) }

    elementWidths.foldLeft(headerWidths){ (currentWidths, nextWidths) =>
      currentWidths.zipAll(nextWidths, 0, 0).map{ case(a,b) => max(a,b) }
    }
  }

  def printWithColumnWidths[T](printStream: PrintStream, widths:Seq[Int])(toShow : T)(implicit showParts: ShowParts[T]): Unit ={
    showParts.showParts(toShow).zipAll(widths, "", 0).foreach{ case(stringRepresentation, width) =>
      printStream print stringRepresentation.padTo(width, columnPadding)
      printStream print columnPadding
    }
    printStream.println()
  }


}

