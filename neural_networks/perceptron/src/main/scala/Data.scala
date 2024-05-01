package cipn

class Data (data: List[List[String]], numberOfFeatures: Int, oneHotEncoding: Map[String, List[Double]]) {
  var features = data.map(x => x.take(numberOfFeatures).map(_.toDouble))
  var labels: List[String] = data.map(x => x.last)
  var encodedLabels: List[List[Double]] = labels.map(oneHotEncoding(_))
}