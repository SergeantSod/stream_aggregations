package stream_aggregations.output

import java.text.{DecimalFormat, DecimalFormatSymbols}
import java.util.Locale

import stream_aggregations.aggregation.TupleComposition

trait Show[T] {
  def show(showable: T): String
}

trait LowPriorityShowImplicits{
  implicit def fallbackShow[T]: Show[T] = new Show[T] {
    override def show(showable: T): String = showable.toString
  }
}

object Show extends LowPriorityShowImplicits {

  implicit def stringShow: Show[String] = new Show[String] {
    override def show(showable: String): String = showable
  }

  implicit def optionShow[T](implicit s: Show[T]): Show[Option[T]] = new Show[Option[T]] {
    override def show(showable: Option[T]): String = showable.map(s.show).getOrElse("???")
  }

  implicit lazy val doubleShow: Show[Double] = {
    //We do this in order to get rounding to the right number of digits, while circumventing localization, since it
    //messes with the tests and generally any other system that might assume a specific comma separator
    val decimalFormat = new DecimalFormat("#.#####", new DecimalFormatSymbols(Locale.ROOT))

    new Show[Double] {
      override def show(showable: Double): String = decimalFormat.format(showable)
    }
  }
}




