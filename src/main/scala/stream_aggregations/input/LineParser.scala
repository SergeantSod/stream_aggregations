package stream_aggregations.input

import stream_aggregations.input.extractors.{ADouble, AnInt, APairOf}

/**
  * A parser that consumes a single line and can be mapped to some other result type.
  * @tparam R The type of the result
  */
trait LineParser[R] {
  outer =>

  /**
    * Parses a line and returns a result
    * @param line The line to parse
    * @return
    */
  def parse(line: String): R

  /**
    * Combines this parser with a PartialFunction and returns a new parser that feeds each of its results through the function
    * and fails wherever the partialfunction is not defined.
    *
    * @param mapping
    * @tparam T
    * @return
    */
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

  /**
    * A trivial parser that parses each line as itself. Useful as the root of composition.
    */
  object Raw extends LineParser[String] {
    override def parse(line: String): String = line
  }

}