package stream_aggregations.aggregation

import stream_aggregations.UnitSpec
import default_aggregations._

class RollingWindowAggregatorSpec extends UnitSpec {
  "A rolling window aggregation" - {
    lazy val upToThreeElements = Aggregation(Option(0)){ (current, _:Int) =>
      current map { _ + 1} filter { _ <= 3 }
    }

    lazy val rollingWindowAggregation = RollingWindowAggregator(sum[Int] ||| last[Int])(upToThreeElements)

    "should aggregate over rolling windows" in {
      forAll{ sequenceOfInts: Seq[Int] =>
        (rollingWindowAggregation over sequenceOfInts).toList should ==={
          sequenceOfInts.tails.toList.init.map{ someTail =>
            val window = someTail.take(3)
            window.sum -> Some(window.last)
          }
        }
      }
    }

    case class TraversableWithCounting[T](underlyingTraversable: Traversable[T]) extends Traversable[T] {

      private var counter = 0

      def iterations:Int = counter

      override def foreach[U](f: (T) => U): Unit = {
        counter +=1
        underlyingTraversable.foreach(f)
      }
    }

    "should traverse the underlying stream only lazily" in {
      forAll{ sequenceOfInts: Seq[Int] =>
        val withCounting = TraversableWithCounting(sequenceOfInts)

        rollingWindowAggregation over withCounting

        withCounting.iterations should ===(0)
      }
    }

    "should traverse the underlying stream only once" in {
      forAll{ sequenceOfInts: Seq[Int] =>
        val withCounting = TraversableWithCounting(sequenceOfInts)

        (rollingWindowAggregation over withCounting).foreach{ _ => }

        withCounting.iterations should ===(1)
      }
    }
  }
}
