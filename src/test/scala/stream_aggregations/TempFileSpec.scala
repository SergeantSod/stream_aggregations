package stream_aggregations

import java.io.{BufferedWriter, File, FileWriter}

trait TempFileSpec {
  def createTempFileWithText(text: String): File = {
    val tempFile = File.createTempFile("testData", "txt")

    val writer = new BufferedWriter(new FileWriter(tempFile))
    writer.write(text)
    writer.close()

    tempFile.deleteOnExit()
    tempFile
  }
}
