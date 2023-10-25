# Genetic-Programming-Classification-Breast-Cancer-Dataset
Breast Cancer Wisconsin (Diagnostic) Data Set. Predict whether the cancer is benign or malignant using Genetic Programming

## GP Classification Algorithms

To solve the classification problem, I followed the following steps:

1. Dataset Split: I randomly split the dataset into an 80:20 ratio, where 80% of the data was used for training and 20% for testing. The training data was saved in a file called "training_data.data," and the remaining 20% was saved in a file called "testing_data.data."

2. Initial Population Generation: I initialized the population using the population.evolve() function with the following parameters:
• Number of Generations: 50 (as specified in the assignment)
• Population Size: 100 (as specified in the assignment)
• Crossover Rate: 0.5 (chosen to balance exploration and exploitation. This rate allows for diverse combinations of solutions while refining good ones)
• Mutation Rate: 0.2 (chosen to maintain population diversity and prevent premature convergence)
• Max tree depth of 10 (It allows the trees to have a sufficient level of complexity to capture relationships and patterns in the data while preventing overfitting.)

For generating the initial population, I used the ramped half and half tree generation method. This method combines the full and grow methods, creating trees of varying depths. This ensures exploration at different levels, facilitating the discovery of both shallow and deep solutions and promoting diversity.

3. Fitness Function: The fitness function is implemented in the getFitness() function within the Population class. It takes a decision tree and a dataset as inputs and returns the fitness value. To calculate fitness, the function uses the classifyInstances method of the DecisionTree class. This method classifies each instance in the dataset using the decision tree, and the number of correctly classified instances is recorded. The fitness value is then calculated as the ratio of correct classifications to the total number of instances in the dataset.

The fitness value obtained from this function represents the accuracy of the decision tree on the given dataset. A higher fitness value indicates better performance in correctly classifying instances.

4. Selection: I employed tournament selection for parent selection during crossover. The tournamentSelection method in the Population class implements this selection method.
During crossover, a parent tree is selected using the tournament selection function with a tournament size of 5. The tournamentSelection function randomly selects 5 trees from the population and chooses the one with the highest fitness as the parent for crossover.

5. Genetic Operators: The genetic operators used in my implementation are crossover and mutation.

a. Crossover: The crossover operator combines genetic material from two parent trees to create new offspring trees. In my code, the crossover method performs the crossover operation. It takes a child tree and a parent tree as input. The child tree's root attribute is replaced with the parent tree's root attribute, resulting in the child inheriting a portion of the parent's genetic material.

b. Mutation: The mutation operator introduces random changes in the genetic material of an individual tree. In the code, the mutation method performs the mutation operation. It takes a tree as input and randomly modifies its attributes. There is a 20% chance of either changing the root attribute to a random attribute from the predefined attribute set or recursively applying the mutation operation to the child branches of the tree.
Both crossover and mutation operations contribute to the exploration and exploitation of the search space. Crossover combines good attributes from different individuals, while mutation introduces random changes that can potentially lead to new and improved solutions. These genetic operators help the algorithm search for better solutions over multiple generations.

6. Population Replacement: I used generational replacement as the population replacement method in my implementation.
Generational replacement involves creating a new population of individuals in each generation, replacing the entire previous population. In my code, the evolve method performs the population replacement. It initializes a new population of decision trees with random individuals and evolves this population over multiple generations.
In each generation, the algorithm applies crossover and mutation genetic operators to create new individuals (trees) based on the existing population. The fitness of each individual is evaluated based on its performance on the training dataset. The best individual (tree) is determined based on its fitness and is stored as the best tree so far. The process continues for the specified number of generations.
By replacing the entire population in each generation, the generational replacement strategy allows the algorithm to explore the search space and potentially discover better solutions over time. The best individual from each generation (the best tree) is preserved, ensuring that the algorithm does not lose the best solution found so far.

7. Termination Condition: The termination condition in my implementation is a fixed number of generations (50). The evolve method runs for a specified number of generations, which is passed as a parameter to the method.
