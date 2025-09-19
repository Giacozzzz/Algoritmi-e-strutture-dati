package ex3_4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.Comparator;

public class Prim {

    /*
        Method to compute the minimum spanning forest using Prim's algorithm.
        Takes a graph as input and returns a collection of edges forming the minimum spanning forest.
        Iterates over each connected component to ensure all are covered, using a priority queue to
        select edges with the lowest weight for each component.
    */
    public static <V, L extends Number> Collection<? extends AbstractEdge<V, L>> minimumSpanningForest(Graph<V, L> graph) {
        Set<V> visited = new HashSet<>();  // Tracks visited nodes
        List<AbstractEdge<V, L>> mstEdges = new ArrayList<>();  // Stores edges in the minimum spanning forest
        EdgeComparator<V, L> edgeComparator = new EdgeComparator<>();

        // Loop over all nodes to handle disconnected components
        for (V startNode : graph.getNodes()) {
            if (visited.contains(startNode)) continue;  // Skip if the node is already visited

            // Priority queue to store edges by increasing weight
            PriorityQueue<AbstractEdge<V, L>> priorityQueue = new PriorityQueue<>(edgeComparator);

            // Visit the starting node and add its edges to the priority queue
            visitNode(graph, startNode, visited, priorityQueue);

            // Process edges in the priority queue to grow the minimum spanning tree
            while (!priorityQueue.isEmpty()) {
                AbstractEdge<V, L> edge = priorityQueue.poll();  // Get the minimum edge

                V to = edge.getEnd();
                if (visited.contains(to)) continue;  // Skip if the destination node is already visited

                mstEdges.add(edge);
                visitNode(graph, to, visited, priorityQueue);
            }
        }

        return mstEdges;
    }

    /*
        Helper method to visit a node and add its edges to the priority queue.
        Takes the graph, the node to visit, a set of visited nodes, and a priority queue as parameters.
        For each neighbor of the node, if it hasn't been visited, creates an edge and adds it to the queue.
    */
    private static <V, L extends Number> void visitNode(Graph<V, L> graph, V node, Set<V> visited, PriorityQueue<AbstractEdge<V, L>> priorityQueue) {
        visited.add(node);  // Mark the node as visited

        // For each neighbor, if it hasn't been visited, add the edge to the priority queue
        for (V neighbor : graph.getNeighbours(node)) {
            if (!visited.contains(neighbor)) {
                L weight = graph.getLabel(node, neighbor);  // Get the weight of the edge
                priorityQueue.add(new Edge<>(node, neighbor, weight));  // Add edge to priority queue
            }
        }
    }

    /*
        Main method to initialize the graph, read data from a CSV file, and compute the minimum spanning forest.
        Expects three command-line arguments: whether the graph is oriented, if it’s labelled, and the file path for the CSV.
        After reading the file, it computes and prints the number of nodes, edges, and total weight of the forest.
    */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Usage: java Prim <oriented> <labelled> <csv_file_path>");
            return;
        }

        boolean oriented = Boolean.parseBoolean(args[0]);
        boolean labelled = Boolean.parseBoolean(args[1]);
        Graph<String, Double> graph = new Graph<>(oriented, labelled);

        // Read data from the CSV file and populate the graph
        String csvFilePath = args[2];
        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length < 3) continue;

                String from = parts[0].trim();
                String to = parts[1].trim();
                double weight = Double.parseDouble(parts[2].trim());

                graph.addNode(from);
                graph.addNode(to);
                graph.addEdge(from, to, weight);
            }
        } catch (IOException e) {
            System.err.println("Error reading the CSV file: " + e.getMessage());
            return;
        } catch (NumberFormatException e) {
            System.err.println("Error parsing weight: " + e.getMessage());
            return;
        }

        // Calculate the minimum spanning forest
        Collection<? extends AbstractEdge<String, Double>> mstEdges = minimumSpanningForest(graph);

        System.out.println("Number of nodes: " + graph.numNodes());
        System.out.println("Number of edges: " + mstEdges.size());

        double totalWeight = mstEdges.stream().mapToDouble(edge -> edge.getLabel().doubleValue()).sum();
        System.out.printf("Total weight: %.2f km%n", totalWeight);
    }

    //Comparator class for comparing edges by weight, used in the priority queue.
    public static class EdgeComparator<V, L extends Number> implements Comparator<AbstractEdge<V, L>> {

        @Override
        public int compare(AbstractEdge<V, L> edge1, AbstractEdge<V, L> edge2) {
            return Double.compare(edge1.getLabel().doubleValue(), edge2.getLabel().doubleValue());
        }
    }
}

