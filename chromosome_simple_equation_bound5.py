from random import choices, randrange, random, uniform
from chromosome import Chromosome
from typing import Tuple
from copy import deepcopy

class ChoromsomeSimpleEquation(Chromosome):
    def __init__(self, x: int, y: int) -> None:
        self.x = x
        self.y = y
        
    def fitness(self) -> float:
        # f(x, y) = (1 − x)² + 100(y − x²)²
        return (1 - self.x)**2 + (100 * (self.y - self.x**2)**2)
    
    @classmethod
    def random_instance(cls) -> Chromosome:
        # Return chromosome with random x and y values between -10 and 10
        # ChoromsomeSimpleEquation(randrange(-10,11), randrange(-10, 11))
        
        # Go from float range from -10 to 10
        return ChoromsomeSimpleEquation(uniform(-5, 5), uniform(-5, 5))
    
    def crossover(self, other: Chromosome) -> Tuple[Chromosome, Chromosome]:
        child1 = deepcopy(self)
        child2 = deepcopy(other)
        child1.y = other.y
        child2.y = self.y
        return child1, child2
        
    def mutate(self) -> None:
        # Mutate x or y with 50% and limit the 
        # values between -10 and 10
        if random() > .5:
            self.x = uniform(-5, 5)
        else:
            self.y = uniform(-5, 5)
            
        
