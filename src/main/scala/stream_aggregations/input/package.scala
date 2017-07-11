package stream_aggregations

import scala.io.Source

package object input {
  //TODO this doesn't close the file, use loan pattern instead.
  def openFile(pathName: String):TraversableOnce[String]= Source.fromFile(pathName).getLines()
}
