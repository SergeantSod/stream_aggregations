package stream_aggregations

import scala.io.Source

package object input {

  def readingFrom(pathName: String)(block: Traversable[String] => Unit):Unit={
    val source = Source.fromFile(pathName)
    try {
      block(source.getLines().toTraversable) //TODO This actually packages stuff up in a Stream, which sucks
    } finally {
      source.close()
    }
  }
}
