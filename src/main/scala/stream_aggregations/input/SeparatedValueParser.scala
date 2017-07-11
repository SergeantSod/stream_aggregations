package stream_aggregations.input

class SeparatedValueParser(separatorRegex: String) extends LineParser[Array[String]] {
  override def parse(line: String): Array[String] = {
    line.split(separatorRegex)
  }
}
