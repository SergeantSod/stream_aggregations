package stream_aggregations

import scala.io.Source

package object input {
  def openFile(pathName: String):TraversableOnce[String]= Source.fromFile(pathName).getLines()
}
