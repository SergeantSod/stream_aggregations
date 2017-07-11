package stream_aggregations.aggregation

import stream_aggregations.UnitSpec

class TupleCompositionSpec extends UnitSpec {
  "The TupleComposition type class" - {

    //TODO Potentially move these methods into the implementation, so it can just be imported from there? Only if we use it in multiple places and find that it is less verbose.
    def compose[A, B](a: A, b: B)(implicit composition: TupleComposition[A, B]): composition.R = {
      composition.compose(a, b)
    }

    def decompose[A, B, R](r: R)(implicit composition: TupleComposition.Aux[A, B, R]): (A, B) = {
      composition.decompose(r)
    }

    "when composing" - {
      "should compose pairs" in {
        compose(1, true) should ===((1, true))
      }

      "should compose 3-tuples" in {
        compose((1, true), "test") should ===((1, true, "test"))
      }

      "should compose 4-tuples" in {
        compose((1, true, "test"), 2.3) should ===((1, true, "test", 2.3))
      }

      "should compose 5-tuples" in {
        compose((1, true, "test", 2.3), 'test) should ===((1, true, "test", 2.3, 'test))
      }
    }

    "when decomposing" - {
      "should decompose pairs" in {
        decompose((1, true)) should ===((1, true))
      }

      "should decompose 3-tuples" in {
        decompose((1, true, "test")) should ===(((1, true), "test"))
      }

      "should decompose 4-tuples" in {
        decompose((1, true, "test", 2.3)) should ===(((1, true, "test"), 2.3))
      }

      "should decompose 5-tuples" in {
        decompose((1, true, "test", 2.3, 'test)) should ===(((1, true, "test", 2.3), 'test))
      }
    }
  }
}
