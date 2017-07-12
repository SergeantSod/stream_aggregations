package stream_aggregations.input

import stream_aggregations.input.extractors.{ADouble, AnInt, APairOf}

trait LineParser[R] {
  outer =>

  def parse(line: String): R

  def map[T](mapping: PartialFunction[R, T]): LineParser[T] = new LineParser[T] {
    override def parse(line: String): T = {
      val intermediate = outer.parse(line)
      if (mapping.isDefinedAt(intermediate)) mapping(intermediate)
      else throw new ParseException(line)
    }
  }
}

class ParseException(line: String) extends RuntimeException(s"Could not parse Line:$line")

object LineParser {

  object Raw extends LineParser[String] {
    override def parse(line: String): String = line
  }

}