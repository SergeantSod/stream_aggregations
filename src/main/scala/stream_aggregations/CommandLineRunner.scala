package stream_aggregations

import stream_aggregations.aggregation.RollingWindowAggregator
import stream_aggregations.tuples.TupleComposition._
import stream_aggregations.aggregation.default_aggregations._
import stream_aggregations.input.InputParser
import stream_aggregations.output.TablePrinter

/**
  * The command line runner. I.e. the main entry point. Reads from a file and prints a table to stdout.
  */

object CommandLineRunner {

  def main(arguments: Array[String]): Unit = arguments match {
    case Array(filePath) => aggregateValuesFromFile(filePath)
    case _ => println("Please pass the path to a file as the one and only argument.")
  }

  private def aggregateValuesFromFile(filePath: String) = {

    val timeStamps  = { t:(Int, Double) => t._1 }
    val priceRatios = { t:(Int, Double) => t._2 }

    val aggregation = (last[Int] of timeStamps)     |||
                      (last[Double] of priceRatios) |||
                       count                        |||
                      (sum[Double] of priceRatios)  |||
                      (min[Double] of priceRatios)  |||
                      (max[Double] of priceRatios)

    val aggregator = RollingWindowAggregator(aggregation){
      optionalBinop[Int]{ (oldOne, newOne) =>
        if( (newOne - oldOne) <= 60) Some(oldOne)
        else None
      } of timeStamps
    }

    input.readingFrom(filePath){ lines =>
      val parsed = lines.map(InputParser.parse)

      val tablePrinter = new TablePrinter[(Option[Int], Option[Double], Int, Double, Option[Double], Option[Double])](Seq("T", "V", "N", "RS", "MinV", "MaxV"))

      tablePrinter.printTo(Console.out)(aggregator.over(parsed))
    }
  }
}
