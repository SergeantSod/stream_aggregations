package stream_aggregations

object CommandLineRunner {
  def main(arguments: Array[String]): Unit = {
    if(arguments.length==1){

    } else{
      println("Please pass the path to a file as the one and only argument.")
    }
  }
}
