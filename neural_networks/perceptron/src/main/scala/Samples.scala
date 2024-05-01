package cipn

object Samples {

  def shuffleAndDivide(
                        list: List[List[String]], test: Double, validation: Double
                      ): List[List[List[String]]] = {
    val shuffled = scala.util.Random.shuffle(list)
    val testSize = (list.size * test).toInt
    val validationSize = (list.size * validation).toInt
    val trainingSize = list.size - testSize - validationSize
    List(
      shuffled.take(trainingSize),
      shuffled.slice(trainingSize, trainingSize + validationSize),
      shuffled.drop(trainingSize + validationSize)
    )
  }
}
