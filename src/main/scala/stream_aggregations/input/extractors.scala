package stream_aggregations.input

object extractors {
  object AnInt {
    def unapply(s : String) : Option[Int] = try {
      //TODO Dry this up
      Some(s.toInt)
    } catch {
      case _ : NumberFormatException => None
    }
  }

  object ADouble {
    def unapply(s : String) : Option[Double] = try {
      //TODO Dry this up
      Some(s.toDouble)
    } catch {
      case _ : NumberFormatException => None
    }
  }
}
