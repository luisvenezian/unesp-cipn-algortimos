# Natural Computing Algorithms

This repository contains code developed during a master's degree program in natural computing. The implemented algorithms focus on optimization and search techniques using artificial intelligence.

## Contents

1. **`otimizacao.py`**: This notebook demonstrates an iterative approach of two AI algorithms for optimization and search: Hill Climbing and Simulated Annealing. It provides a step-by-step visualization of the algorithms using matplotlib.

2. **`chromosome.py`**: An abstract class used for implementing various chromosome representations. Derived classes include `chromosome_bitstring.py`, `chromosome_bitstring_max_function.py`, and `chromosome_simple_equation.py`.

3. **`genetic_algorithm.py`**: This file contains a general class for running genetic algorithms with support for any kind of chromosome representation. The `run()` method has been modified multiple times to return generation age, fitness value, or the instance of the best chromosome. The structure of this code is inspired by David Kopec's approach outlined in the book "Classic Computer Science Problems in Python".

4. **`main.py`**: Entry point for the codebase.

5. **Notebooks**:
   - `dispersao.ipynb` and `3d_and_series.ipynb` were used for ad-hoc graph plotting and do not significantly contribute to the project's value. However, they are kept for future reference and potential reuse.

## Example Results

Here are some visualizations of the optimization process:

Visualization of the function \( f(x, y) = (1 − x)^2 + 100(y − x^2)^2 \), showcasing the results of the genetic algorithm optimization.
![3D Graph](3d.png)   
  
Evolution of the function result at each generation of the population initialized.
![Fitness Graph](fitness.png) 
