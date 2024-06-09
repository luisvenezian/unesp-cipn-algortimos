import numpy as np

def sphere_function(theta):
    return np.sum(theta**2)

def attractant_repellent(population, d_attract, w_attract, h_repellant, w_repellant):
    S, p = population.shape
    J_attract = np.zeros(S)
    J_repellent = np.zeros(S)
    
    for i in range(S):
        for j in range(S):
            if i != j:
                distance = np.sum((population[i] - population[j])**2)
                J_attract[i] += -d_attract * np.exp(-w_attract * distance)
                J_repellent[i] += h_repellant * np.exp(-w_repellant * distance)
    
    return J_attract, J_repellent

def bfo_algorithm(
    objective_function,     # Objective function to minimize
    p,                      # Dimension of the problem
    S=10,                   # Number of bacteria
    N_c=5,                  # Number of chemotactic steps
    N_s=4,                  # Number of swim steps
    N_re=4,                 # Number of reproduction steps
    N_ed=2,                 # Number of elimination-dispersal events
    C=0.1,                  # Step size
    d_attract=0.1,          # Attractant coefficient
    w_attract=0.2,          # Attractant weight
    h_repellant=0.1,        # Repellant coefficient
    w_repellant=10          # Repellant weight
    ):
    # Initialize population
    population = np.random.uniform(-5.12, 5.12, (S, p))
    costs = np.apply_along_axis(objective_function, 1, population)
    
    for l in range(N_ed):               # For each elimination-dispersal event
        for k in range(N_re):           # For each reproduction step
            for j in range(N_c):        # For each chemotactic step 
                for i in range(S):      # For each bacterium 
                                        # Then J(i, j, k, l) where i ranges from 1 to S
                                        
                    print("J(i, j, k, l) = J(%d, %d, %d, %d)" % (i, j, k, l))
                    # Chemotactic step
                    direction = np.random.uniform(-1, 1, p)
                    direction = direction / np.linalg.norm(direction)
                    new_position = population[i] + C * direction
                    new_cost = objective_function(new_position)
                    
                    # Save into csv 
                    with open('output/bfo.csv', 'a') as f:
                        # i, j, k, l, x, y, cost
                        f.write(f"{i},{j},{k},{l},{new_position[0]},{new_position[1]},{new_cost}\n")
                    
                    # Calculate attractant and repellent effects
                    J_attract, J_repellent = attractant_repellent(population, d_attract, w_attract, h_repellant, w_repellant)
                    
                    effective_cost = new_cost + J_attract[i] + J_repellent[i]
                    
                    if effective_cost < costs[i]:
                        population[i] = new_position
                        costs[i] = new_cost + J_attract[i] + J_repellent[i]
                        
                        # Swim
                        for m in range(N_s):
                            new_position = population[i] + C * direction
                            new_cost = objective_function(new_position)
                            J_attract, J_repellent = attractant_repellent(population, d_attract, w_attract, h_repellant, w_repellant)
                            effective_cost = new_cost + J_attract[i] + J_repellent[i]
                            
                            if effective_cost < costs[i]:
                                population[i] = new_position
                                costs[i] = new_cost + J_attract[i] + J_repellent[i]
                            else:
                                break
                    else:
                        continue
            
            # Reproduction step
            sorted_indices = np.argsort(costs)
            population = population[sorted_indices]
            costs = costs[sorted_indices]
            population[S//2:] = population[:S//2] + np.random.uniform(-1, 1, (S//2, p))
            costs[S//2:] = np.apply_along_axis(objective_function, 1, population[S//2:])
        
        # Elimination-dispersal step
        for i in range(S):
            if np.random.rand() < 0.1:
                population[i] = np.random.uniform(-5.12, 5.12, p)
                costs[i] = objective_function(population[i])
    
    best_index = np.argmin(costs)
    return population[best_index], costs[best_index]

if __name__ == "__main__":
    # Example usage
    best_position, best_cost = bfo_algorithm(sphere_function, p=2)

    print(f"Best Position: {best_position}")
    print(f"Best Cost: {best_cost}")
