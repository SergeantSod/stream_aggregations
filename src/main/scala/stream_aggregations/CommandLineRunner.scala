package stream_aggregations

import stream_aggregations.aggregation.{Aggregation, RollingWindowAggregator}
import stream_aggregations.output.ShowParts
import stream_aggregations.aggregation.TupleComposition._
import stream_aggregations.aggregation.default_aggregations._
import stream_aggregations.input.SeparatedValueParser
import stream_aggregations.input.extractors.{ADouble, AnInt}

object CommandLineRunner {

  def main(arguments: Array[String]): Unit = arguments match {
    case Array(filePath) => aggregateValuesFromFile(filePath)
    case _ => println("Please pass the path to a file as the one and only argument.")
  }

  private def aggregateValuesFromFile(filePath: String) = {
    val lineParser = new SeparatedValueParser("\\s+").map{
      case Array(AnInt(int), ADouble(double)) => int -> double
    }

    val timeStamps = { t:(Int, Double) => t._1 }
    val priceRatios = { t:(Int, Double) => t._2 }

    val aggregator = RollingWindowAggregator((sum[Double] on priceRatios) ||| (last[Int] on timeStamps)){
      Aggregation(None: Option[Int]){ (last, next: Int) =>
        //TODO This should be expressable more easily
        last orElse Some(next) filter { 60 >= next - _ }
      } on timeStamps
    }

    input.readingFrom(filePath){ lines =>
      val parsed = lines.map(lineParser.parse)

      aggregator.over(parsed).foreach(println)
    }
  }
}
