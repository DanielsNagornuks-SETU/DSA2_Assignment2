package applied_computing.setu;

import java.util.ArrayList;

public class Graph<T> {

    private ArrayList<GraphNode<T>> nodes = new ArrayList<>();

    public void addNode(GraphNode<T> node) {
        nodes.add(node);
    }

    public ArrayList<GraphNode<T>> getNodes() {
        return nodes;
    }

    public void setNodes(ArrayList<GraphNode<T>> nodes) {
        this.nodes = nodes;
    }

    // Dijkstra's
    public ArrayList<GraphNode<T>> shortestPathByWeight(GraphNode<T> source, GraphNode<T> destination) {
        return null;
    }

    // BFS
    public ArrayList<GraphNode<T>> shortestPathByNodes(GraphNode<T> source, GraphNode<T> destination) {
        return null;
    }

    // DFS
    public ArrayList<ArrayList<GraphNode<T>>> allPathsBetweenNodes(GraphNode<T> source, GraphNode<T> destination) {
        return null;
    }

}
