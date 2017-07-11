package stream_aggregations.input

//TODO Consider moving this into the input package object instead
object extractors {

  class NumberExtractor[T](toNumber:String=>T) {
    def unapply(string : String) : Option[T] = try {
      Some(toNumber(string))
    } catch {
      case _ : NumberFormatException => None
    }

  }

  object AnInt extends NumberExtractor[Int](_.toInt)
  object ADouble extends NumberExtractor[Double](_.toDouble)
}
