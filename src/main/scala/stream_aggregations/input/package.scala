package stream_aggregations

import stream_aggregations.input.LineParser.Raw
import stream_aggregations.input.extractors._

import scala.io.Source

package object input {

  def readingFrom(pathName: String)(block: Iterator[String] => Unit):Unit={
    val source = Source.fromFile(pathName)
    try {
      block(source.getLines())
    } finally {
      source.close()
    }
  }

  val InputParser: LineParser[(Int, Double)] = Raw.map { case APairOf(AnInt(intValue), ADouble(doubleValue)) =>
    (intValue, doubleValue)
  }
}
