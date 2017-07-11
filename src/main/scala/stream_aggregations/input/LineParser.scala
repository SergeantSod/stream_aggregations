package stream_aggregations.input

//TODO Consider variance annotation
trait LineParser[R] { outer =>

  def parse(line:String): R

  def map[T](mapping: PartialFunction[R,T]): LineParser[T] = new LineParser[T] {
    override def parse(line: String): T = {
      val intermediate = outer.parse(line)
      if(mapping.isDefinedAt(intermediate)) mapping(intermediate)
      else ???
    }
  }
}

