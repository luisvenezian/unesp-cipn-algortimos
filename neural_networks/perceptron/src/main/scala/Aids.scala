package cipn

object Aids extends Dataset {
  private val path: String = "src/main/scala/aids.csv"
  val oneHotEncoding: Map[String, List[Double]] = Map(
    "0" -> List(1.0, 0.0),
    "1" -> List(0.0, 1.0)
  )
  val oneHotDecoding: Map[List[Double], String] = oneHotEncoding.map(_.swap)
  val dataSets: List[Data] = {
    val source = scala.io.Source.fromFile(path)
    val lines: List[String] = source.getLines().toList
    val fileData: List[List[String]] = lines.map(_.split(",").toList)
    source.close()
    Samples.shuffleAndDivide(fileData, test = 0.4).map(Data(_, 22, oneHotEncoding))
  }
  val train: Data = dataSets(0)
  val test: Data = dataSets(1)
}
