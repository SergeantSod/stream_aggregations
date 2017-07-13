package stream_aggregations.output

import stream_aggregations.UnitSpec

class ShowPartsSpec extends UnitSpec {
  "The ShowParts type class" - {
    "should show parts of a high-arity tuple" in {
      implicitly[ShowParts[(Int, Double, Boolean, Option[String], String)]] showParts(1, 6.2, true, Some("some"), "string") should ==={
        Seq("1", "6.2", "true", "some", "string")
      }
    }

    "should show parts of a Seq" in {
      implicitly[ShowParts[Seq[Int]]] showParts Seq(3,6,8) should ==={
        Seq("3", "6", "8")
      }
    }

    "should fall back to showing an object as its single only part" in {
      implicitly[ShowParts[Double]] showParts 1.666666 should ===(Seq("1.66667"))
    }
  }
}
