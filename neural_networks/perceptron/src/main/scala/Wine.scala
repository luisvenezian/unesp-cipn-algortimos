package cipn

object Wine extends Dataset{
  private val path: String = "src/main/scala/wine.csv"
  val oneHotEncoding: Map[String, List[Double]] = Map(
    "1" -> List(1.0, 0.0, 0.0),
    "2" -> List(0.0, 1.0, 0.0),
    "3" -> List(0.0, 0.0, 1.0)
  )
  val oneHotDecoding: Map[List[Double], String] = oneHotEncoding.map(_.swap)
  val dataSets: List[Data] = {
    val source = scala.io.Source.fromFile(path)
    val lines: List[String] = source.getLines().toList
    val fileData: List[List[String]] = lines.map(_.split(",").toList)
    source.close()
    Samples.shuffleAndDivide(fileData, test = 0.3).map(Data(_, 13, oneHotEncoding))
  }
  val train: Data = dataSets(0)
  val test: Data = dataSets(1)


  def validate(net: MultiOutputPerceptron): Unit =  {
    val results: List[String] = test.features.map(t => oneHotDecoding(net.predict(t)))
    val test_features_labels = test.labels.zip(results).zip(test.encodedLabels.map(oneHotDecoding(_)))
    for (((features, predicted), actual) <- test_features_labels) {
      var result: String = if (predicted == actual) "Correct" else "Incorrect"
      println(s"Features: $features, Predicted: $predicted, Actual: $actual Result: $result")
    }
  }
}
