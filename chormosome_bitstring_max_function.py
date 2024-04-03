from random import choices
from chromosome import Chromosome
from typing import Tuple
from math import pi, sin

class ChoromsomeBitStringMaxFunction(Chromosome):
    def __init__(self, bitstring: str) -> None:
        self.bitstring = bitstring
        
    def fitness(self) -> float:
        
        # convert bitstring to a number
        value = int(self.bitstring, 2)
        
        if value > 1000:
            value = 1000
        value /= 1000
 
        # apply the function
        return (2**(-2 * (((value - 0.1) / 0.9) ** 2))) * ((sin(5*pi*value)**6))
        
    
    @classmethod
    def random_instance(cls) -> Chromosome:
        # Return a random bitstring with 12 characters
        return ChoromsomeBitStringMaxFunction(''.join(choices('01', k=10)))
    
    def crossover(self, other: Chromosome) -> Tuple[Chromosome, Chromosome]:
        
        # Crossover the bitstrings in the middle
        return (
            ChoromsomeBitStringMaxFunction(self.bitstring[:5] + other.bitstring[5:]),
            ChoromsomeBitStringMaxFunction(other.bitstring[:5] + self.bitstring[5:])
        )
        
    def mutate(self) -> None:
        # Mutate one random position in the bitstring
        new_bit = list(self.bitstring)
        new_bit[choices(range(10))[0]] = choices('01')[0]
        self.bitstring = ''.join(new_bit)
        
