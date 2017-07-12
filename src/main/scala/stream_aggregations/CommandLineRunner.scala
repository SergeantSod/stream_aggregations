package stream_aggregations

import stream_aggregations.aggregation.{Aggregation, RollingWindowAggregator}
import stream_aggregations.output.{ShowParts, TablePrinter}
import stream_aggregations.aggregation.TupleComposition._
import stream_aggregations.aggregation.default_aggregations._
import stream_aggregations.input.LineParser.InputParser
import stream_aggregations.input.LineParser
import stream_aggregations.input.extractors.{ADouble, AnInt}

object CommandLineRunner {

  def main(arguments: Array[String]): Unit = arguments match {
    case Array(filePath) => aggregateValuesFromFile(filePath)
    case _ => println("Please pass the path to a file as the one and only argument.")
  }

  private def aggregateValuesFromFile(filePath: String) = {

    val timeStamps = { t:(Int, Double) => t._1 }
    val priceRatios = { t:(Int, Double) => t._2 }

    val aggregation = (last[Int] on timeStamps)     |||
                      (last[Double] on priceRatios) |||
                       count                        |||
                      (sum[Double] on priceRatios)  |||
                      (min[Double] on priceRatios)  |||
                      (max[Double] on priceRatios)

    val aggregator = RollingWindowAggregator(aggregation){
      Aggregation(None: Option[Int]){ (last, next: Int) =>
        //TODO This should be expressable more easily, maybe optionaBinop helps
        last orElse Some(next) filter { 60 >= next - _ }
      } on timeStamps
    }

    input.readingFrom(filePath){ lines =>
      val parsed = lines.map(InputParser.parse)

      val tablePrinter = new TablePrinter[(Option[Int], Option[Double], Int, Double, Option[Double], Option[Double])](Seq("T", "V", "N", "RS", "MinV", "MaxV"))

      tablePrinter.printTo(Console.out)(aggregator.over(parsed))
    }
  }
}
