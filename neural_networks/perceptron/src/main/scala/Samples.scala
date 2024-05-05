package cipn

object Samples {

  def shuffleAndDivide(
                        list: List[List[String]], test: Double
                      ): List[List[List[String]]] = {
    val shuffled = scala.util.Random.shuffle(list)
    val testSize = (list.size * test).toInt
    val trainingSize = list.size - testSize 
    List(
      shuffled.take(trainingSize), // training
      shuffled.drop(trainingSize)  // test
    )
  }
}
