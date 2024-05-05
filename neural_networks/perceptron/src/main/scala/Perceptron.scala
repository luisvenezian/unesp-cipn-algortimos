package cipn
import scala.util.Random
import ujson._
import os._
import scala.concurrent.{Future, Await}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

trait activationFunction {
  def limiar(x: Double): Double = if (x > 0) 1 else 0

  def softmax(x: Array[Double]): Array[Double] = {
    val maxVal = x.max
    val exps = x.map(xi => math.exp(xi - maxVal))
    val sum = exps.sum
    exps.map(_ / sum)
  }
}


class MultiOutputPerceptron (
                              val dataset: Dataset,
                              val MAX_ITERATIONS: Int = 100,
                              val LEARNING_RATE: Double = 0.000001
                            ) extends activationFunction
{
  val features: List[List[Double]] = dataset.train.features
  val expected_output: List[List[Double]] = dataset.train.encodedLabels
  val random = new Random()
  val input_size = features.head.size
  val output_size = expected_output.head.size
  var weights = Array.fill(output_size, input_size)(random.nextDouble() * 0.01)
  var logs = List[Obj]()
  var bias = 0.0
  enum DatasetKind:
    case Training, Test

  def train(): Unit = {
    for (i <- 0 until MAX_ITERATIONS) {
      println(s"Epoch $i")
      for (j <- features.indices) {
        val x = features(j).toArray
        val y = expected_output(j).toArray
        val z = weights.map(w => w.zip(x).map { case (a, b) => a * b }.sum + bias)
        val o = softmax(z)
        val delta = y.zip(o).map { case (a, b) => a - b }
        val gradient = delta.map(d => x.map(_ * d))
        weights = weights.zip(gradient).map { case (a, b) => a.zip(b).map { case (c, d) => c + LEARNING_RATE * d  } }
        bias = bias + LEARNING_RATE * delta.sum
      }

      val mseValidationFuture = Future(mse(DatasetKind.Test))
      val mseTrainingFuture = Future(mse(DatasetKind.Training))
      val accuracyValidationFuture = Future(validate(DatasetKind.Test))
      val accuracyTrainingFuture = Future(validate(DatasetKind.Training))

      val mseValidation = Await.result(mseValidationFuture, Duration.Inf)
      val mseTraining = Await.result(mseTrainingFuture, Duration.Inf)
      val accuracyValidation = Await.result(accuracyValidationFuture, Duration.Inf)
      val accuracyTraining = Await.result(accuracyTrainingFuture, Duration.Inf)

      logs = ujson.Obj.from(
        Map(
          "epoch" -> i,
          "mse_validation" -> mseValidation,
          "mse_training" -> mseTraining,
          "accuracy_validation" -> accuracyValidation,
          "accuracy_training" -> accuracyTraining,
          "weights" -> weights.toList.map(_.toList).toList,
          "bias" -> bias
        )
      ) :: logs
    }

    // Save logs JSON file
    val ts: String = System.currentTimeMillis().toString
    val logsPath = os.pwd / "logs" / "executions" / "aids" / s"output-$ts.json"
    os.write.over(logsPath, ujson.write(logs))

    // create the test data and predictions for saving as csv
    val testFeatures = dataset.test.features
    val testPredictions = testFeatures.map(predict).map(Aids.oneHotDecoding(_))
    val testPredictionsWithLabels = testPredictions.zip(dataset.test.labels)
    val testPredictionsWithLabelsAndFeatures = testPredictionsWithLabels.zip(testFeatures)
    val testPredictionsWithLabelsCsv = testPredictionsWithLabelsAndFeatures.map { case ((a, b), c) => s"${c.mkString(",")},$a,$b" }.mkString("\n")
    // add header
    val header = dataset.test.features.head.indices.map(i => s"feature$i").mkString(",") + ",expected,predicted\n"
    val predictionsPath = os.pwd / "logs" / "predictions" / "aids" / s"predictions-$ts.csv"
    os.write.over(predictionsPath, header + testPredictionsWithLabelsCsv)

    println("TS: " + ts)

  }

  def validate(datasetKind: DatasetKind): Double = {
    val features = datasetKind match {
      case DatasetKind.Training => dataset.train.features
      case DatasetKind.Test => dataset.test.features
    }
    val expected_output = datasetKind match {
      case DatasetKind.Training => dataset.train.labels
      case DatasetKind.Test => dataset.test.labels
    }
    val predictions = features.map(predict).map(Aids.oneHotDecoding(_))
    val accuracy = expected_output.zip(predictions).count { case (a, b) => a == b } / features.size.toDouble
    accuracy
  }

  def predict(x: List[Double]): List[Double] = {
    val z = weights.map(w => w.zip(x).map { case (a, b) => a * b }.sum)
    val softmaxOutput = softmax(z).toList
    val closestKey = Aids.oneHotEncoding.minBy { case (_, v) =>
      v.zip(softmaxOutput).map { case (a, b) => math.abs(a - b) }.sum
    }._2
    closestKey
  }

  def mse(datasetKind: DatasetKind): Double = {
    val features = datasetKind match {
      case DatasetKind.Training => dataset.train.features
      case DatasetKind.Test => dataset.test.features
    }
    val expected_output = datasetKind match {
      case DatasetKind.Training => dataset.train.encodedLabels
      case DatasetKind.Test => dataset.test.encodedLabels
    }
    val predictions = features.map(predict)
    val mse = expected_output.zip(predictions).map { case (a, b) => a.zip(b).map { case (c, d) => math.pow(c - d, 2) }.sum }.sum / features.size
    mse
  }

  def confusion_matrix(): Unit = {
    val features = dataset.test.features
    val expected_output = dataset.test.labels
    val predictions = features.map(predict).map(Wine.oneHotDecoding(_))
    val confusionMatrix = expected_output.zip(predictions).groupBy(identity).mapValues(_.size)
    print("Confusion Matrix: ")
    println(confusionMatrix)
  }
}
