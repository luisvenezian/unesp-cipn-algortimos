package cipn
import scala.util.Random

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
                              val MAX_ITERATIONS: Int = 1000,
                              val LEARNING_RATE: Double = 0.1
                            ) extends activationFunction
{
  val features: List[List[Double]] = dataset.train.features
  val expected_output: List[List[Double]] = dataset.train.encodedLabels
  val random = new Random()
  val input_size = features.head.size
  val output_size = expected_output.head.size
  var weights = Array.fill(output_size, input_size)(random.nextDouble)
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
    }
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
}
