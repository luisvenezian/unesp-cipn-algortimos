from random import choices
from chromosome import Chromosome
from typing import Tuple

class ChoromsomeBitString(Chromosome):
    def __init__(self, bitstring: str) -> None:
        self.bitstring = bitstring
        
    def fitness(self) -> float:
        # Return the hamming distance from the target bitstring 
        target = "111101101111"
        hamming_distance = sum(c1 != c2 for c1, c2 in zip(self.bitstring, target))
        return 1 / (hamming_distance + 1)
    
    @classmethod
    def random_instance(cls) -> Chromosome:
        # Return a random bitstring with 12 characters
        return ChoromsomeBitString(''.join(choices('01', k=12)))
    
    def crossover(self, other: Chromosome) -> Tuple[Chromosome, Chromosome]:
        
        # Crossover the bitstrings in the middle
        return (
            ChoromsomeBitString(self.bitstring[:6] + other.bitstring[6:]),
            ChoromsomeBitString(other.bitstring[:6] + self.bitstring[6:])
        )
        
    def mutate(self) -> None:
        # Mutate one random position in the bitstring
        new_bit = list(self.bitstring)
        new_bit[choices(range(12))[0]] = choices('01')[0]
        self.bitstring = ''.join(new_bit)
        
