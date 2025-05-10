package applied_computing.setu;

import java.util.*;

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
    public ArrayList<GraphNode<T>> shortestPathByNodes(Queue<ArrayList<GraphNode<T>>> partialPaths, ArrayList<GraphNode<T>> encountered, GraphNode<T> destination) {
        if (partialPaths.isEmpty()) return null;
        ArrayList<GraphNode<T>> currentPath = partialPaths.remove(); //Get first item (next path to consider) off agenda
        GraphNode<T> currentNode = currentPath.get(0); //The first item in the next path is the current node
        if (currentNode.getValue().equals(destination.getValue())) return currentPath; //If that's the goal, we've found our path (so return it)
        if (encountered == null) encountered = new ArrayList<>(); //First node considered in search so create new (empty) encountered list
        encountered.add(currentNode); //Record current node as encountered so it isn't revisited again
        for (GraphNode<T> adjNode : currentNode.getAdjacencyList().keySet()) //For each adjacent node
            if (!encountered.contains(adjNode)) { //If it hasn't already been encountered
                ArrayList<GraphNode<T>> newPath = new ArrayList<>(currentPath); //Create a new path list as a copy of the current/next path
                newPath.add(0, adjNode); //And add the adjacent node to the front of the new copy
                partialPaths.add(newPath); //Add the new path to the end of agenda (end->BFS!)
            }
        return shortestPathByNodes(partialPaths, encountered, destination);
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

    /* Interfacing method (order of waypoints matters) */
    public ArrayList<GraphNode<Station>> shortestPathBetweenStationsWithOrder(GraphNode<Station> source, GraphNode<Station> destination, double laneChangePenalty, ArrayList<GraphNode<Station>> waypointStations, HashSet<GraphNode<Station>> stationsToAvoid) {
        ArrayList<GraphNode<Station>> route = new ArrayList<>();
        ArrayList<GraphNode<Station>> currentRoute;
        ArrayList<GraphNode<Station>>waypoints = new ArrayList<>(waypointStations);
        while (!waypoints.isEmpty()) {
            currentRoute = shortestPathToOneOfStationsWithOrder(source, waypoints.remove(0), laneChangePenalty, stationsToAvoid);
            if (currentRoute == null) return null;
            source = currentRoute.remove(currentRoute.size() - 1);
            waypoints.remove(source);
            route.addAll(currentRoute);
        }
        currentRoute = shortestPathToOneOfStationsWithOrder(source, destination, laneChangePenalty, stationsToAvoid);
        if (currentRoute == null) return null;
        route.addAll(currentRoute);
        return route;
    }

    /* Dijkstra's with order for waypoints */
    public ArrayList<GraphNode<Station>> shortestPathToOneOfStationsWithOrder(GraphNode<Station> source, GraphNode<Station> destination, double laneChangePenalty, HashSet<GraphNode<Station>> stationsToAvoid) {
        source.setNodeValue(0);
        HashSet<GraphNode<Station>> considered = new HashSet<>(stationsToAvoid);
        PriorityQueue<GraphNode<Station>> agenda = new PriorityQueue<>();
        agenda.offer(source);
        GraphNode<Station> currentNode;
        do {
            currentNode = agenda.poll();
            if (considered.contains(currentNode)) continue;
            considered.add(currentNode);
            if (currentNode.getValue().equals(destination.getValue())) {
                ArrayList<GraphNode<Station>> shortestPath = new ArrayList<>();
                shortestPath.add(currentNode);
                while (!currentNode.getValue().equals(source.getValue())) {
                    currentNode = currentNode.getReturnNode();
                    shortestPath.add(0, currentNode);
                }
                for (GraphNode<Station> adjNode : agenda) {
                    adjNode.setNodeValue(Double.MAX_VALUE);
                    adjNode.setReturnNode(null);
                }
                for (GraphNode<Station> adjNode : considered) {
                    adjNode.setNodeValue(Double.MAX_VALUE);
                    adjNode.setReturnNode(null);
                }
                return shortestPath;
            }
            for (GraphNode<Station> adjNode : currentNode.getAdjacencyList().keySet()) {
                if (!considered.contains(adjNode)) {
                    double penalty = 0;
                    if (!currentNode.getValue().equals(source.getValue()) && !StationManager.stationsShareLanes(adjNode.getValue(), currentNode.getReturnNode().getValue()))
                        penalty = laneChangePenalty;
                    double newWeight = currentNode.getNodeValue() + currentNode.getAdjacencyList().get(adjNode) + penalty;
                    if (newWeight < adjNode.getNodeValue()) {
                        adjNode.setNodeValue(newWeight);
                        adjNode.setReturnNode(currentNode);
                        agenda.offer(adjNode);
                    }
                }
            }
        } while (!agenda.isEmpty());
        return null;
    }



    /* Archived methods */

    /* Generic method for implementing Dijkstra's algorithm */
    public ArrayList<GraphNode<T>> shortestPathByWeight(GraphNode<T> source, GraphNode<T> destination) {
        source.setNodeValue(0);
        HashSet<GraphNode<T>> considered = new HashSet<>();
        PriorityQueue<GraphNode<T>> agenda = new PriorityQueue<>();
        agenda.offer(source);
        GraphNode<T> currentNode;
        do {
            currentNode = agenda.poll();
            if (considered.contains(currentNode)) continue;
            considered.add(currentNode);
            if (currentNode.getValue().equals(destination.getValue())) {
                ArrayList<GraphNode<T>> shortestPath = new ArrayList<>();
                shortestPath.add(currentNode);
                while (!currentNode.getValue().equals(source.getValue())) {
                    currentNode = currentNode.getReturnNode();
                    shortestPath.add(0, currentNode);
                }
                for (GraphNode<T> adjNode : agenda) {
                    adjNode.setNodeValue(Double.MAX_VALUE);
                    adjNode.setReturnNode(null);
                }
                for (GraphNode<T> adjNode : considered) {
                    adjNode.setNodeValue(Double.MAX_VALUE);
                    adjNode.setReturnNode(null);
                }
                return shortestPath;
            }
            for (GraphNode<T> adjNode : currentNode.getAdjacencyList().keySet()) {
                if (!considered.contains(adjNode)) {
                    double newWeight = currentNode.getNodeValue() + currentNode.getAdjacencyList().get(adjNode);
                    if (newWeight < adjNode.getNodeValue()) {
                        adjNode.setNodeValue(newWeight);
                        adjNode.setReturnNode(currentNode);
                        agenda.offer(adjNode);
                    }
                }
            }
        } while (!agenda.isEmpty());
        return null;
    }

    /* Interfacing method (no order) */
    public ArrayList<GraphNode<Station>> shortestPathBetweenStationsNoOrder(GraphNode<Station> source, GraphNode<Station> destination, double laneChangePenalty, HashSet<GraphNode<Station>> waypointStations, HashSet<GraphNode<Station>> stationsToAvoid) {
        ArrayList<GraphNode<Station>> route = new ArrayList<>();
        ArrayList<GraphNode<Station>> currentRoute;
        HashSet<GraphNode<Station>>waypoints = new HashSet<>(waypointStations);
        while (!waypoints.isEmpty()) {
            currentRoute = shortestPathToOneOfStationsNoOrder(source, waypoints, laneChangePenalty, stationsToAvoid);
            if (currentRoute == null) return null;
            source = currentRoute.remove(currentRoute.size() - 1);
            waypoints.remove(source);
            route.addAll(currentRoute);
        }
        waypoints.add(destination);
        currentRoute = shortestPathToOneOfStationsNoOrder(source, waypoints, laneChangePenalty, stationsToAvoid);
        if (currentRoute == null) return null;
        route.addAll(currentRoute);
        return route;
    }

    /* Dijkstra's with no order for waypoints */
    public ArrayList<GraphNode<Station>> shortestPathToOneOfStationsNoOrder(GraphNode<Station> source, HashSet<GraphNode<Station>> waypoints, double laneChangePenalty, HashSet<GraphNode<Station>> stationsToAvoid) {
        source.setNodeValue(0);
        HashSet<GraphNode<Station>> considered = new HashSet<>(stationsToAvoid);
        PriorityQueue<GraphNode<Station>> agenda = new PriorityQueue<>();
        agenda.offer(source);
        GraphNode<Station> currentNode;
        do {
            currentNode = agenda.poll();
            if (considered.contains(currentNode)) continue;
            considered.add(currentNode);
            GraphNode<Station> waypointFound = null;
            for (GraphNode<Station> waypoint : waypoints)
                if (currentNode.getValue().equals(waypoint.getValue())) {
                    waypointFound = waypoint;
                }
            if (waypointFound != null) {
                ArrayList<GraphNode<Station>> shortestPath = new ArrayList<>();
                shortestPath.add(currentNode);
                while (!currentNode.getValue().equals(source.getValue())) {
                    currentNode = currentNode.getReturnNode();
                    shortestPath.add(0, currentNode);
                }
                for (GraphNode<Station> adjNode : agenda) {
                    adjNode.setNodeValue(Double.MAX_VALUE);
                    adjNode.setReturnNode(null);
                }
                for (GraphNode<Station> adjNode : considered) {
                    adjNode.setNodeValue(Double.MAX_VALUE);
                    adjNode.setReturnNode(null);
                }
                waypointFound.setNodeValue(Double.MAX_VALUE);
                waypointFound.setReturnNode(null);
                return shortestPath;
            }
            for (GraphNode<Station> adjNode : currentNode.getAdjacencyList().keySet()) {
                if (!considered.contains(adjNode)) {
                    double penalty = 0;
                    if (!currentNode.getValue().equals(source.getValue()) && !StationManager.stationsShareLanes(adjNode.getValue(), currentNode.getReturnNode().getValue()))
                        penalty = laneChangePenalty;
                    double newWeight = currentNode.getNodeValue() + currentNode.getAdjacencyList().get(adjNode) + penalty;
                    if (newWeight < adjNode.getNodeValue()) {
                        adjNode.setNodeValue(newWeight);
                        adjNode.setReturnNode(currentNode);
                        agenda.offer(adjNode);
                    }
                }
            }
        } while (!agenda.isEmpty());
        return null;
    }

}
