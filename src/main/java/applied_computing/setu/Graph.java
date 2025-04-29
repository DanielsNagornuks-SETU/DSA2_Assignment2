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

    // BFS
    public ArrayList<GraphNode<T>> shortestPathByNodes(ArrayList<ArrayList<GraphNode<T>>> partialPaths, ArrayList<GraphNode<T>> encountered, GraphNode<T> destination) {
        if(partialPaths.isEmpty()) return null; //Search failed
        ArrayList<GraphNode<T>> nextPath=partialPaths.remove(0); //Get first item (next path to consider) off agenda
        GraphNode<T> currentNode=nextPath.get(0); //The first item in the next path is the current node
        if(currentNode.getValue().equals(destination.getValue())) return nextPath; //If that's the goal, we've found our path (so return it)
        if(encountered==null) encountered=new ArrayList<>(); //First node considered in search so create new (empty) encountered list
        encountered.add(currentNode); //Record current node as encountered so it isn't revisited again
        for(GraphNode<T> adjNode : currentNode.getAdjacencyList().keySet()) //For each adjacent node
            if(!encountered.contains(adjNode)) { //If it hasn't already been encountered
                ArrayList<GraphNode<T>> newPath=new ArrayList<>(nextPath); //Create a new path list as a copy of the current/next path
                newPath.add(0,adjNode); //And add the adjacent node to the front of the new copy
                partialPaths.add(newPath); //Add the new path to the end of agenda (end->BFS!)
            }
        return shortestPathByNodes(partialPaths,encountered,destination);
    }

    // DFS
    public ArrayList<ArrayList<GraphNode<T>>> allPathsBetweenNodes(GraphNode<T> source, ArrayList<GraphNode<T>> encountered ,GraphNode<T> destination) {
        ArrayList<ArrayList<GraphNode<T>>> result=null, temp2;
        if(source.getValue().equals(destination.getValue())) { //Found it
            ArrayList<GraphNode<T>> temp=new ArrayList<>(); //Create new single solution path list
            temp.add(source); //Add current node to the new single path list
            result=new ArrayList<>(); //Create new "list of lists" to store path permutations
            result.add(temp); //Add the new single path list to the path permutations list
            return result; //Return the path permutations list
        }

        if(encountered==null) encountered=new ArrayList<>(); //First node so create new (empty) encountered list
        encountered.add(source); //Add current node to encountered list
        for(GraphNode<T> adjNode : source.getAdjacencyList().keySet()){
            if(!encountered.contains(adjNode)) {
                temp2=allPathsBetweenNodes(adjNode,new ArrayList<>(encountered),destination); //Use clone of encountered list
//for recursive call!
                if(temp2!=null) { //Result of the recursive call contains one or more paths to the solution node
                    for(ArrayList<GraphNode<T>> x : temp2) //For each partial path list returned
                        x.add(0,source); //Add the current node to the front of each path list
                    if(result==null) result=temp2; //If this is the first set of solution paths found use it as the result
                    else result.addAll(temp2); //Otherwise append them to the previously found paths
                }
            }
        }
        return result;
    }

    // Dijkstra's
    public ArrayList<GraphNode<T>> shortestPathByWeight(GraphNode<T> source, GraphNode<T> destination) {
        return null;
    }

}
