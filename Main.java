import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

class DecisionTree {
    private Node root;
    int FP = 0, FN = 0;

    public DecisionTree(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }

    public String classifyInstance(Data instance) {
        Node currentNode = root;
        while (currentNode.getTrueBranch() != null && currentNode.getFalseBranch() != null) {
            String attribute = currentNode.getAttribute();
            String attributeValue = instance.getAttributeValue(attribute);

            if (attributeValue != null && attributeValue.equals("true")) {
                currentNode = currentNode.getTrueBranch();
            } else {
                currentNode = currentNode.getFalseBranch();
            }
        }
        return currentNode.getAttribute();
    }

    public int classifyInstances(ArrayList<Data> instances) {
        int correctClassifications = 0;
        for (Data instance : instances) {
            String classification = classifyInstance(instance);
            if (classification.equals("false") && instance.getClassLabel().equals("no-recurrence-events")
                    || (classification.equals("true") && instance.getClassLabel().equals("recurrence-events"))) {
                correctClassifications++;
            }
        }
        return correctClassifications;
    }

    public int FP(ArrayList<Data> instances) {
        int correctClassifications = 0;
        for (Data instance : instances) {
            String classification = classifyInstance(instance);
            if (classification.equals("false") && instance.getClassLabel().equals("recurrence-events")) {
                correctClassifications++;
            }
        }
        return correctClassifications;
    }

    public int FN(ArrayList<Data> instances) {
        int correctClassifications = 0;
        for (Data instance : instances) {
            String classification = classifyInstance(instance);
            if (classification.equals("true") && instance.getClassLabel().equals("no-recurrence-events")) {
                correctClassifications++;
            }
        }
        return correctClassifications;
    }
}

class Node {
    private String attribute;
    private Node trueBranch;
    private Node falseBranch;

    public Node(String attribute) {
        this.attribute = attribute;
    }

    public String getAttribute() {
        return attribute;
    }

    public void setAttribute(String attribute) {
        this.attribute = attribute;
    }

    public Node getTrueBranch() {
        return trueBranch;
    }

    public void setTrueBranch(Node trueBranch) {
        this.trueBranch = trueBranch;
    }

    public Node getFalseBranch() {
        return falseBranch;
    }

    public void setFalseBranch(Node falseBranch) {
        this.falseBranch = falseBranch;
    }
}

class Population {
    private ArrayList<DecisionTree> trees;
    private DecisionTree bestTree;
    private ArrayList<Data> trainingDataset;

    public Population() {
        trees = new ArrayList<>();
        bestTree = null;
        trainingDataset = new ArrayList<>();
    }

    public DecisionTree getBestTree() {
        return bestTree;
    }

    public void initializePopulation(int populationSize, int maxTreeDepth) {
        for (int i = 0; i < populationSize; i++) {
            Node root = createRandomTree(maxTreeDepth);
            DecisionTree tree = new DecisionTree(root);
            trees.add(tree);
        }
    }

    private Node createRandomTree(int maxDepth) {
        if (maxDepth == 0 || Math.random() < 0.5) {
            String classification = Math.random() < 0.5 ? "true" : "false";
            return new Node(classification);
        }

        String attribute = getRandomAttribute();
        Node root = new Node(attribute);
        root.setTrueBranch(createRandomTree(maxDepth - 1));
        root.setFalseBranch(createRandomTree(maxDepth - 1));

        return root;
    }

    private String getRandomAttribute() {
        String[] attributes = { "age", "menopause", "tumor-size", "inv-nodes", "node-caps", "deg-malig", "breast",
                "breast-quad", "irradiat" };

        return attributes[(int) (Math.random() * attributes.length)];
    }

    private void crossover(DecisionTree child, DecisionTree parent) {
        child.getRoot().setAttribute(parent.getRoot().getAttribute());
    }

    private void mutation(DecisionTree tree) {
        Node root = tree.getRoot();
        String[] attributes = { "age", "menopause", "tumor-size", "inv-nodes", "node-caps", "deg-malig", "breast",
                "breast-quad", "irradiat" };

        if (Math.random() < 0.5) {
            root.setAttribute(attributes[(int) (Math.random() * attributes.length)]);
        } else {
            if (root.getTrueBranch() != null) {
                mutation(new DecisionTree(root.getTrueBranch()));
            }
            if (root.getFalseBranch() != null) {
                mutation(new DecisionTree(root.getFalseBranch()));
            }
        }
    }

    public double getFitness(DecisionTree tree, ArrayList<Data> dataset) {

        int correctClassifications = tree.classifyInstances(dataset);
        // System.out.println(correctClassifications);
        return (double) correctClassifications / dataset.size();
    }

    public double getFitness(DecisionTree tree) {
        int correctClassifications = tree.classifyInstances(trainingDataset);
        return (double) correctClassifications / trainingDataset.size();
    }

    private DecisionTree tournamentSelection(ArrayList<DecisionTree> population, int tournamentSize) {
        DecisionTree bestTree = null;

        for (int i = 0; i < tournamentSize; i++) {
            DecisionTree randomTree = population.get((int) (Math.random() * population.size()));
            if (bestTree == null || getFitness(randomTree) > getFitness(bestTree)) {
                bestTree = randomTree;
            }
        }

        return bestTree;
    }

    public void evolve(int numGenerations, int populationSize, double crossoverRate, double mutationRate,
        ArrayList<Data> dataset) {
    initializePopulation(populationSize, 10);
    bestTree = trees.get(0);
    trainingDataset = dataset;
    double[] accuracies = new double[numGenerations];

    for (int i = 0; i < numGenerations; i++) {
        for (int j = 0; j < populationSize; j++) {
            DecisionTree tree = trees.get(j);
            if (Math.random() < crossoverRate) {
                DecisionTree parent = tournamentSelection(trees, 5); // Tournament selection with size 5
                crossover(tree, parent);
            }
            if (Math.random() < mutationRate) {
                mutation(tree);
            }
        }

        for (DecisionTree tree : trees) {
            double fitness = getFitness(tree, dataset);
            if (fitness > getFitness(bestTree, dataset)) {
                bestTree = tree;
            }
        }

        double averageAccuracy = 0;
        for (DecisionTree tree : trees) {
            double accuracy = getFitness(tree);
            averageAccuracy += accuracy;
        }
        averageAccuracy /= populationSize;
        accuracies[i] = averageAccuracy;

;
    }

    System.out.println("Evolution completed.");
    System.out.println("Best Decision Tree:");
    printDecisionTree(bestTree);
    System.out.println("Average Accuracy across Generations:");
    for (int i = 0; i < numGenerations; i++) {
        System.out.println("Generation " + (i + 1) + ": " + (accuracies[i] * 100) + "%");
    }
}


    private void printDecisionTree(DecisionTree bestTree2) {
        
    }

    private double classifyInstances(ArrayList<Data> trainingDataset2) {
        return 0;
    }
    
}

class Main {
    public static double calculateFMeasure(int truePositives, int falsePositives, int falseNegatives) {
        double precision = (double) truePositives / (truePositives + falsePositives);
        double recall = (double) truePositives / (truePositives + falseNegatives);

        double fMeasure = 2 * (precision * recall) / (precision + recall);
        return fMeasure;
    }
    public static void main(String[] args) {
        String trainingFilePath = "training_data.data";
        String testFilePath = "test.txt";
        ArrayList<Data> trainingDataset = readDataset(trainingFilePath);
        ArrayList<Data> testDataset = readDataset(testFilePath);

        if (trainingDataset.isEmpty() || testDataset.isEmpty()) {
            System.out.println("Failed to read the dataset.");
            return;
        }

        Population population = new Population();
        population.evolve(50, 100, 0.6, 0.2, trainingDataset);
        double accuracy = 0;
        while (accuracy == 0) {
            long startTime = System.nanoTime();
            DecisionTree bestTree = population.getBestTree();
            printDecisionTree(bestTree);
            if (bestTree != null) {
                int correctClassifications = bestTree.classifyInstances(testDataset);
                int FalseP = bestTree.FP(testDataset);
                int FalseN = bestTree.FN(testDataset);
                System.out.println(calculateFMeasure(correctClassifications, FalseP, FalseN));
                accuracy = (double) correctClassifications / testDataset.size();
                long endTime = System.nanoTime();

                double executionTimeSeconds = (endTime - startTime) / 1_000_000_000.0;
        
                System.out.println("Run time: " + executionTimeSeconds + " seconds");
            } else {
                System.out.println("No best tree found.");
            }
        }
        System.out.println("Accuracy: " + accuracy * 100 + "%");
    }

    private static ArrayList<Data> readDataset(String filePath) {
        ArrayList<Data> dataset = new ArrayList<>();

        try {
            File file = new File(filePath);
            Scanner scanner = new Scanner(file);

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] attributes = line.split(",");

                String[] dataAttributes = new String[attributes.length - 1];
                for (int i = 1; i < attributes.length; i++) {
                    dataAttributes[i - 1] = attributes[i];
                }

                String classLabel = attributes[0];
                Data data = new Data(dataAttributes, classLabel);
                dataset.add(data);
            }

            scanner.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return dataset;
    }

    private static void printDecisionTree(DecisionTree tree) {
        System.out.println("Decision Tree:");
        printNode(tree.getRoot(), 0);
    }

    private static void printNode(Node node, int level) {
        if (node == null) {
            return;
        }

        for (int i = 0; i < level; i++) {
            System.out.print("\t");
        }
        System.out.println(node.getAttribute());

        printNode(node.getTrueBranch(), level + 1);
        printNode(node.getFalseBranch(), level + 1);
    }
}

class Data {
    private String[] attributes;
    private String classLabel;

    public Data(String[] attributes, String classLabel) {
        this.attributes = attributes;
        this.classLabel = classLabel;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public String getAttribute(int index) {
        return attributes[index];
    }

    public String getAttributeValue(String attribute) {
        int index = getAttributeIndex(attribute);
        if (index != -1) {
            return attributes[index];
        }
        return null;
    }

    public String getClassLabel() {
        return classLabel;
    }

    private int getAttributeIndex(String attribute) {
        for (int i = 0; i < attributes.length; i++) {
            if (attributes[i].equals(attribute)) {
                return i;
            }
        }
        return -1;
    }
}
