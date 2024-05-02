package cipn
import scala.util.Random
import ujson._
import os._

trait activationFunction {
  def limiar(x: Double): Double = if (x > 0) 1 else 0

  def softmax(x: Array[Double]): Array[Double] = {
    val exps = x.map(math.exp)
    val sum = exps.sum
    exps.map(_ / sum)
  }
}


class MultiOutputPerceptron (
                              val dataset: Dataset,
                              val MAX_ITERATIONS: Int = 100,
                              val LEARNING_RATE: Double = 0.1
                            ) extends activationFunction
{
  val features: List[List[Double]] = dataset.train.features
  val expected_output: List[List[Double]] = dataset.train.encodedLabels
  val random = new Random()
  val input_size = features.head.size
  val output_size = expected_output.head.size
  var weights = Array.fill(output_size, input_size)(random.nextDouble)
  var logs = List[Obj]()
  enum DatasetKind:
    case Training, Validation, Test

  def train(): Unit = {
    for (i <- 0 until MAX_ITERATIONS) {
      for (j <- 0 until features.size) {
        val x = features(j).toArray
        val y = expected_output(j).toArray
        val z = weights.map(w => w.zip(x).map { case (a, b) => a * b }.sum)
        val o = softmax(z)
        val delta = y.zip(o).map { case (a, b) => a - b }
        val gradient = delta.map(d => x.map(_ * d))
        weights = weights.zip(gradient).map { case (a, b) => a.zip(b).map { case (c, d) => c + LEARNING_RATE * d } }
      }

      logs = ujson.Obj.from(
        Map(
          "iteration" -> i,
          "mse_trainig" -> mse(DatasetKind.Training),
          "mse_validation" -> mse(DatasetKind.Validation),
          "mse_test" -> mse(DatasetKind.Test),
          "accuracy_training" -> validate(DatasetKind.Training),
          "accuracy_validation" -> validate(DatasetKind.Validation),
          "accuracy_test" -> validate(DatasetKind.Test),
          "weights" -> weights.toList.map(_.toList).toList
        )
      ) :: logs
    }

    // Save logs JSON file
    val ts: String = System.currentTimeMillis().toString
    val logsPath = os.pwd / "logs" / s"output-$ts.json"
    os.write.over(logsPath, ujson.write(logs))

  }

  def validate(datasetKind: DatasetKind): Double = {
    val features = datasetKind match {
      case DatasetKind.Training => dataset.train.features
      case DatasetKind.Validation => dataset.validation.features
      case DatasetKind.Test => dataset.test.features
    }
    val expected_output = datasetKind match {
      case DatasetKind.Training => dataset.train.labels
      case DatasetKind.Validation => dataset.validation.labels
      case DatasetKind.Test => dataset.test.labels
    }
    val predictions = features.map(predict).map(Iris.oneHotDecoding(_))
    val accuracy = expected_output.zip(predictions).count { case (a, b) => a == b } / features.size.toDouble
    accuracy
  }

  def predict(x: List[Double]): List[Double] = {
    val z = weights.map(w => w.zip(x).map { case (a, b) => a * b }.sum)
    softmax(z).toList.map(_.round)
  }

  def mse(datasetKind: DatasetKind): Double = {
    val features = datasetKind match {
      case DatasetKind.Training => dataset.train.features
      case DatasetKind.Validation => dataset.validation.features
      case DatasetKind.Test => dataset.test.features
    }
    val expected_output = datasetKind match {
      case DatasetKind.Training => dataset.train.encodedLabels
      case DatasetKind.Validation => dataset.validation.encodedLabels
      case DatasetKind.Test => dataset.test.encodedLabels
    }
    val predictions = features.map(predict)
    val mse = expected_output.zip(predictions).map { case (a, b) => a.zip(b).map { case (c, d) => math.pow(c - d, 2) }.sum }.sum / features.size
    mse
  }
}
