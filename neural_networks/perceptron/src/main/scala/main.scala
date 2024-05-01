package cipn

@main
def main(): Unit = {
  val network: MultiOutputPerceptron = MultiOutputPerceptron(Iris)
  network.train()
  print(network.validate(network.DatasetKind.Validation))
}