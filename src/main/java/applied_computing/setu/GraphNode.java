package applied_computing.setu;

import java.util.HashMap;

public class GraphNode<T> {

    private T value;
    private HashMap<GraphNode<T>, Double> adjacencyList = new HashMap<>();

    GraphNode(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public HashMap<GraphNode<T>, Double> getAdjacencyList() {
        return adjacencyList;
    }

    public void setAdjacencyList(HashMap<GraphNode<T>, Double> adjacencyList) {
        this.adjacencyList = adjacencyList;
    }

    public void connectNode(GraphNode<T> target, double weight) {
        adjacencyList.put(target, weight);
        target.getAdjacencyList().put(this, weight);
    }

}
