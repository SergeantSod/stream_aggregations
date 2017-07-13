package stream_aggregations

import java.io.{BufferedWriter, File, FileWriter}

import org.scalactic.{AbstractStringUniformity, TypeCheckedTripleEquals, Uniformity}
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}
import org.scalactic.StringNormalizations._

class AcceptanceTest extends FeatureSpec with GivenWhenThen with Matchers with TypeCheckedTripleEquals with TempFileSpec with StringNormalization {

  def captureOutputFrom(block: => Unit): String = {
    val stream = new java.io.ByteArrayOutputStream()
    Console.withOut(stream)(block)
    stream.toString()
  }

  val input =
    """1355270609 1.80215
      |1355270621 1.80185
      |1355270646 1.80195
      |1355270702 1.80225
      |1355270702 1.80215
      |1355270829 1.80235
      |1355270854 1.80205
      |1355270868 1.80225
      |1355271000 1.80245
      |1355271023 1.80285
      |1355271024 1.80275
      |1355271026 1.80285
      |1355271027 1.80265
      |1355271056 1.80275
      |1355271428 1.80265
      |1355271466 1.80275
      |1355271471 1.80295
      |1355271507 1.80265
      |1355271562 1.80275
      |1355271588 1.80295""".stripMargin

  val expectedOutput =
    """T          V       N RS      MinV    MaxV
      |---------------------------------------------
      |1355270609 1.80215 1 1.80215 1.80215 1.80215
      |1355270621 1.80185 2 3.604   1.80185 1.80215
      |1355270646 1.80195 3 5.40595 1.80185 1.80215
      |1355270702 1.80225 2 3.6042  1.80195 1.80225
      |1355270702 1.80215 3 5.40635 1.80195 1.80225
      |1355270829 1.80235 1 1.80235 1.80235 1.80235
      |1355270854 1.80205 2 3.6044  1.80205 1.80235
      |1355270868 1.80225 3 5.40665 1.80205 1.80235
      |1355271000 1.80245 1 1.80245 1.80245 1.80245
      |1355271023 1.80285 2 3.6053  1.80245 1.80285
      |1355271024 1.80275 3 5.40805 1.80245 1.80285
      |1355271026 1.80285 4 7.2109  1.80245 1.80285
      |1355271027 1.80265 5 9.01355 1.80245 1.80285
      |1355271056 1.80275 6 10.8163 1.80245 1.80285
      |1355271428 1.80265 1 1.80265 1.80265 1.80265
      |1355271466 1.80275 2 3.6054  1.80265 1.80275
      |1355271471 1.80295 3 5.40835 1.80265 1.80295
      |1355271507 1.80265 3 5.40835 1.80265 1.80295
      |1355271562 1.80275 2 3.6054  1.80265 1.80275
      |1355271588 1.80295 2 3.6057  1.80275 1.80295
      |""".stripMargin

  feature("Reading from a file and printing to the standard output") {
    scenario("Computing aggregates within a rolling time window") {

      Given("a plain text file with timestamps and price ratios")

      val file = createTempFileWithText(input)

      When("I run the stream aggregator and capture its output")

      val actualOutput = captureOutputFrom {
        CommandLineRunner.main(Array(file.getCanonicalPath))
      }

      Then("its output should contain the correct aggregates")

      (actualOutput should ===(expectedOutput))(after being trimmedPerLine)
    }

    scenario("Early exit without arguments"){
      Given("I don't any arguments")
      When("I run the aggregator and capture its ouptu")
      val actualOutput = captureOutputFrom {
        CommandLineRunner.main(Array())
      }
      Then("its output should contain an error message")
      actualOutput should include("pass the path to a file")
    }
  }
}
