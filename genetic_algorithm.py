from __future__ import annotations
from typing import TypeVar, Generic, List, Tuple, Callable
from enum import Enum
from random import choices, random
from heapq import nlargest
from statistics import mean
from chromosome import Chromosome

C = TypeVar('C', bound=Chromosome)
class GeneticAlgorithm(Generic[C]):
    
    # Enum with the selection types Roulette or Tournament
    SelectionType = Enum('SelectionType', 'ROULETTE TOURNAMENT')
    
    def __init__(self, 
                 inital_population: List[C], 
                 threshold: float, 
                 max_generations: int = 100, 
                 mutation_chance: float = 0.01, 
                 crossover_chance: float = 0.7, 
                 selection_type: SelectionType = SelectionType.TOURNAMENT
        ) -> None:
        self.population = inital_population
        self.threshold = threshold
        self.max_generations = max_generations
        self.mutation_chance = mutation_chance
        self.crossover_chance = crossover_chance
        self.selection_type = selection_type
        self.fitness_key: Callable = type(inital_population[0]).fitness
        
    def pick_roulette(self, wheel: List[float]) -> Tuple[C, C]:
        return tuple(choices(self.population, weights=wheel, k=2))
    
    def pick_tournament(self, num_participants: int) -> Tuple[C, C]:
        participants = choices(self.population, k=num_participants)
        return tuple(nlargest(2, participants, key=self.fitness_key))
                 
    def _reproduce_and_replace(self) -> None:
        new_population = []
        while len(new_population) < len(self.population):
            # Parents selection
            if self.selection_type == GeneticAlgorithm.SelectionType.ROULETTE:
                parents = self.pick_roulette([x.fitness() for x in self.population])
            else:
                parents = self.pick_tournament(num_participants=3)
            
            # Reproduction
            if random() < self.crossover_chance:
                new_population.extend(parents[0].crossover(parents[1]))
            else:
                new_population.extend(parents)
                
        # Remove extra children
        if len(new_population) > len(self.population):
            new_population.pop()
            
        self.population = new_population
        
    def _mutate(self) -> None:
        for individual in self.population:
            if random() < self.mutation_chance:
                individual.mutate()
                
    def run(self) -> C:
        best = max(self.population, key=self.fitness_key)
        best_fitness_series = [best.fitness()]
        for generation in range(self.max_generations):
            best_fitness_series.append(best.fitness())
            # Check if the threshold was reached and then return the best individual
            if best.fitness() >= self.threshold:
                return best
            
            # Produces next generation
            print(f'Generation {generation} Best {best.fitness()} Avg {mean(map(self.fitness_key, self.population))}')
            self._reproduce_and_replace()
            self._mutate()
            highest = max(self.population, key=self.fitness_key)
            if highest.fitness() > best.fitness():
                best = highest
                
        return best, best_fitness_series