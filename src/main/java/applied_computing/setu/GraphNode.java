package applied_computing.setu;

import java.util.HashMap;

public class GraphNode<T> implements Comparable<GraphNode<T>> {

    private T value;
    private HashMap<GraphNode<T>, Double> adjacencyList = new HashMap<>();
    private double nodeValue=Double.MAX_VALUE;
    private GraphNode<T> returnNode = null;

    public GraphNode<T> getReturnNode() {
        return returnNode;
    }

    public void setReturnNode(GraphNode<T> returnNode) {
        this.returnNode = returnNode;
    }

    public double getNodeValue() {
        return nodeValue;
    }

    public void setNodeValue(double nodeValue) {
        this.nodeValue = nodeValue;
    }

    public GraphNode(T value) {
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

    @Override
    public int compareTo(GraphNode other) {
        return Double.compare(this.nodeValue, other.nodeValue);
    }

}
