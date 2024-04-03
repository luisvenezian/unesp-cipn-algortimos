from chromosome_bitstring import ChoromsomeBitString
from chromosome_simple_equation import ChoromsomeSimpleEquation
from genetic_algorithm import GeneticAlgorithm 
from chormosome_bitstring_max_function import ChoromsomeBitStringMaxFunction
import time
import math

if __name__ == '__main__':
    
    # ex1
    def chromosome_bitstring():
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

    
    # ex2
    def chromosome_bitstring_max_function():
        i = 10
        results = list()
        while i > 0:
            ini = time.process_time()
            inicial_population = [ChoromsomeBitStringMaxFunction.random_instance() for _ in range(20)]
            result = GeneticAlgorithm(
                inital_population=inicial_population, 
                threshold=1,
                max_generations=10, 
                mutation_chance=1, 
                crossover_chance=.5,
                selection_type=GeneticAlgorithm.SelectionType.ROULETTE
            )
            r = result.run()
            results.append(r.fitness(), time.process_time() - ini)
            i-=1
            
        print(results)

    # ex3
    def chromosome_simple_equation():
        i = 10
        results = list()
        results_fitness = list()
        while i > 0:
            inicial_population = [ChoromsomeSimpleEquation.random_instance() for _ in range(10)]
            result = GeneticAlgorithm(
                inital_population=inicial_population, 
                threshold=math.inf,
                max_generations=100, 
                mutation_chance=.75, 
                crossover_chance=.75,
                selection_type=GeneticAlgorithm.SelectionType.ROULETTE
            )
            r = result.run()
            results.append([r[0].x, r[0].y, r[0].fitness()])
            results_fitness.append(r[1])
            i -= 1
        
        print(results)
        print(results_fitness)
    
    chromosome_simple_equation()