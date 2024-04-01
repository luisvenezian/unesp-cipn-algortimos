from chromosome_bitstring import ChoromsomeBitString
from genetic_algorithm import GeneticAlgorithm 

if __name__ == '__main__':
    
    
    i = 10
    results = list()
    while i > 0:
        inicial_population = [ChoromsomeBitString.random_instance() for _ in range(200)]
        result = GeneticAlgorithm(
            inital_population=inicial_population, 
            threshold=1,
            max_generations=1000, 
            mutation_chance=.5, 
            crossover_chance=1,
            selection_type=GeneticAlgorithm.SelectionType.TOURNAMENT
        )
        r = result.run()
        results.append(r)
        i -= 1
    
    print(results)