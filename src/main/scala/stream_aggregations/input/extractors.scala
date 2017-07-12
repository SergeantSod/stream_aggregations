package stream_aggregations.input

import scala.util.matching.Regex

//TODO Consider moving this into the input package object instead
object extractors {

  class NumberExtractor[T](toNumber:String=>T) {
    def unapply(string : String) : Option[T] = try {
      Some(toNumber(string))
    } catch {
      case _ : NumberFormatException => None
    }

  }

  val APairOf:Regex = "(\\S+)\\s+(\\S+)".r

  object AnInt extends NumberExtractor[Int](_.toInt)
  object ADouble extends NumberExtractor[Double](_.toDouble)
}
