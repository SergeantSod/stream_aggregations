package stream_aggregations

import scala.io.Source

package object input {

  def readingFrom(pathName: String)(block: TraversableOnce[String] => Unit):Unit={
    val source = Source.fromFile(pathName)
    try {
      block(source.getLines())
    } finally {
      source.close()
    }
  }
}
