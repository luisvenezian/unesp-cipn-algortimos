package cipn

@main
def main(): Unit = {
  val network: MultiOutputPerceptron = MultiOutputPerceptron(Aids)
  network.train()
}