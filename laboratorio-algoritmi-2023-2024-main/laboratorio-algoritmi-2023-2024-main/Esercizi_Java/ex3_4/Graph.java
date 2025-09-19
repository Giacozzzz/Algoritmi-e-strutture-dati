package ex3_4;

import java.util.*;

public class Graph<V, L> implements AbstractGraph<V, L> {
    private final boolean directed;
    private final boolean labelled;
    private final Map<V, Map<V, L>> nodesList;
    private int numEdges;

    /*
        Constructor for the Graph class.
        Initializes a graph as either directed or undirected, labelled or unlabelled,
        based on the parameters. Sets up a structure to hold nodes and edges,
        and initializes the edge count to zero.
    */
    Graph(boolean directed, boolean labelled) {
        this.directed = directed;
        this.labelled = labelled;
        nodesList = new HashMap<>();
        numEdges = 0;
    }

    /*
        Checks if the graph is directed.
        Returns true if the graph is directed, otherwise false.
    */
    @Override
    public boolean isDirected() {
        return directed;
    }

    /*
        Checks if the graph is labelled.
        Returns true if the edges in the graph have labels, otherwise false.
    */
    @Override
    public boolean isLabelled() {
        return labelled;
    }

    /*
        Adds a node to the graph.
        Checks if the node already exists. If it doesn’t, creates a new entry in the graph
        for this node with an empty set of connections and returns true.
        If the node already exists, it returns false.
    */
    @Override
    public boolean addNode(V a) {
        if (nodesList.containsKey(a)) return false;
        nodesList.put(a, new HashMap<>());
        return true;
    }

    /*
        Adds an edge between two nodes with an optional label.
        If either node is absent, it returns false. If the graph is unlabelled, sets
        the label to null. Adds an edge from node 'a' to node 'b', and, if undirected,
        also adds the reverse connection from 'b' to 'a'.
        Returns true if the edge is successfully added, otherwise false.
    */
    @Override
    public boolean addEdge(V a, V b, L l) {
        if (!nodesList.containsKey(a) || !nodesList.containsKey(b)) return false;
        if (!labelled) l = null;
        Map<V, L> edges = nodesList.get(a);
        if (edges.containsKey(b)) return false;

        edges.put(b, l);
        if (!directed) {
            nodesList.get(b).put(a, l);
        }
        numEdges++;
        return true;
    }

    /*
        Checks if a node is present in the graph.
        Returns true if the node exists, otherwise false.
    */
    @Override
    public boolean containsNode(V a) {
        return nodesList.containsKey(a);
    }

    /*
        Checks if an edge exists between two nodes.
        Returns true if there is an edge from node 'a' to node 'b', otherwise false.
    */
    @Override
    public boolean containsEdge(V a, V b) {
        return nodesList.containsKey(a) && nodesList.get(a).containsKey(b);
    }

    /*
        Removes a node and all its associated edges.
        Checks if the node exists. If it does, removes it and all its edges,
        updating the edge count accordingly. If the graph is undirected,
        removes any edges connecting other nodes to this node as well.
        Returns true if the node is successfully removed, otherwise false.
    */
    @Override
    public boolean removeNode(V a) {
        if (!nodesList.containsKey(a)) return false;

        numEdges -= nodesList.get(a).size();
        nodesList.remove(a);

        if (!directed) {
            for (Map<V, L> edges : nodesList.values()) {
                if (edges.containsKey(a)) {
                    edges.remove(a);
                    numEdges--;
                }
            }
        }
        return true;
    }

    /*
        Removes an edge between two nodes.
        Checks if the edge exists. If it does, removes the edge from node 'a' to node 'b'.
        If the graph is undirected, also removes the edge from 'b' to 'a'.
        Updates the edge count and returns true if the edge is removed, otherwise false.
    */
    @Override
    public boolean removeEdge(V a, V b) {
        if (!containsEdge(a, b)) return false;

        nodesList.get(a).remove(b);
        if (!directed) {
            nodesList.get(b).remove(a);
        }
        numEdges--;
        return true;
    }

    /*
        Returns the total number of nodes in the graph.
        This is done by returning the size of the nodes list.
    */
    @Override
    public int numNodes() {
        return nodesList.size();
    }

    /*
        Returns the total number of edges in the graph.
        This is tracked by the numEdges variable.
    */
    @Override
    public int numEdges() {
        return numEdges;
    }

    /*
        Returns a collection of all nodes in the graph.
        Extracts the set of keys from the nodes list, which represent all nodes.
    */
    @Override
    public Collection<V> getNodes() {
        return nodesList.keySet();
    }

    /*
        Returns a collection of all edges in the graph.
        Iterates through all nodes and retrieves outgoing edges. For undirected graphs,
        includes only edges from nodes with a lower hash value to those with a higher one
        to avoid duplication.
    */
    @Override
    public Collection<? extends AbstractEdge<V, L>> getEdges() {
        List<AbstractEdge<V, L>> edges = new ArrayList<>();

        for (V node : nodesList.keySet()) {
            for (Map.Entry<V, L> entry : nodesList.get(node).entrySet()) {
                if (directed || node.hashCode() <= entry.getKey().hashCode()) {
                    edges.add(new Edge<>(node, entry.getKey(), entry.getValue()));
                }
            }
        }

        return edges;
    }

    /*
        Retrieves all neighbors of a node.
        If the node is present in the graph, returns its neighbors. Otherwise, returns an empty set.
    */
    @Override
    public Collection<V> getNeighbours(V a) {
        return nodesList.containsKey(a) ? nodesList.get(a).keySet() : Collections.emptySet();
    }

    /*
        Retrieves the label of an edge between two nodes.
        Checks if the edge exists between 'a' and 'b'. If so, returns the label.
        Otherwise, returns null.
    */
    @Override
    public L getLabel(V a, V b) {
        return nodesList.containsKey(a) ? nodesList.get(a).get(b) : null;
    }
}
