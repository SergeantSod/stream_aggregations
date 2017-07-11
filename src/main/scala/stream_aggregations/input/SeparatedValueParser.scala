package stream_aggregations.input
//TODO This need not even be a separate class, but could be the result of mapping over a default Parser[String]
class SeparatedValueParser(separatorRegex: String) extends LineParser[Array[String]] {
  override def parse(line: String): Array[String] = {
    line.split(separatorRegex)
  }
}
