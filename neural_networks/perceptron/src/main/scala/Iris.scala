package cipn

trait Dataset {
  val test: Data
  val train: Data
  val validation: Data
}

object Iris extends Dataset{
  private val path: String = "src/main/scala/iris.csv"
  val oneHotEncoding: Map[String, List[Double]] = Map(
    "Iris-setosa" -> List(1.0, 0.0, 0.0),
    "Iris-versicolor" -> List(0.0, 1.0, 0.0),
    "Iris-virginica" -> List(0.0, 0.0, 1.0)
  )
  val oneHotDecoding: Map[List[Double], String] = oneHotEncoding.map(_.swap)
  val dataSets: List[Data] = {
    val source = scala.io.Source.fromFile(path)
    val lines: List[String] = source.getLines().toList
    val fileData: List[List[String]] = lines.map(_.split(",").toList)
    source.close()
    Samples.shuffleAndDivide(fileData, test = 0.15, validation = 0.15).map(Data(_, 4, oneHotEncoding))
  }
  val train: Data = dataSets(0)
  val test: Data = dataSets(1)
  val validation: Data = dataSets(2)


  def validate(net: MultiOutputPerceptron): Unit =  {
    val results: List[String] = test.features.map(t => oneHotDecoding(net.predict(t)))
    val test_features_labels = test.labels.zip(results).zip(test.encodedLabels.map(oneHotDecoding(_)))
    for (((features, predicted), actual) <- test_features_labels) {
      var result: String = if (predicted == actual) "Correct" else "Incorrect"
      println(s"Features: $features, Predicted: $predicted, Actual: $actual Result: $result")
    }
  }
}
