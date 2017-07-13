package stream_aggregations

import org.scalactic.{AbstractStringUniformity, Uniformity}

trait StringNormalization {
  def trimmedPerLine: Uniformity[String] = new AbstractStringUniformity {
    override def normalized(string: String): String = string.lines.map(_.trim).mkString("\n")
  }
}
