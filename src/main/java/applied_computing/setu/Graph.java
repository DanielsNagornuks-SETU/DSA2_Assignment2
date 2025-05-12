package applied_computing.setu;

import java.util.*;

public class Graph<T> {


    /* Interfacing method for BFS for Graph class*/
    public ArrayList<GraphNode<T>> shortestPathByNodes(GraphNode<T> source, GraphNode<T> destination, HashSet<GraphNode<T>> stationsToAvoid) {
        ArrayList<GraphNode<T>> startPath = new ArrayList<>();
        startPath.add(source);
        Queue<ArrayList<GraphNode<T>>> partialPaths = new LinkedList<>();
        partialPaths.add(startPath);
        ArrayList<GraphNode<T>> resultPath = shortestPathByNodes(partialPaths, null, destination, stationsToAvoid);
        Collections.reverse(resultPath);
        return resultPath;
    }

    /* Interfacing method for BFS for Controller class */
    public ArrayList<GraphNode<T>> shortestPathByNodes(GraphNode<T> source, GraphNode<T> destination, ArrayList<GraphNode<T>> waypointStations, HashSet<GraphNode<T>> stationsToAvoid) {
        ArrayList<GraphNode<T>> route = new ArrayList<>();
        ArrayList<GraphNode<T>> currentRoute;
        ArrayList<GraphNode<T>> waypoints = new ArrayList<>(waypointStations);
        while (!waypoints.isEmpty()) {
            currentRoute = shortestPathByNodes(source, waypoints.remove(0), stationsToAvoid);
            if (currentRoute == null) return null;
            source = currentRoute.remove(currentRoute.size() - 1);
            route.addAll(currentRoute);
        }
        currentRoute = shortestPathByNodes(source, destination, stationsToAvoid);
        if (currentRoute == null) return null;
        route.addAll(currentRoute);
        return route;
    }

    /* BFS */
    public ArrayList<GraphNode<T>> shortestPathByNodes(Queue<ArrayList<GraphNode<T>>> partialPaths, ArrayList<GraphNode<T>> encountered, GraphNode<T> destination, HashSet<GraphNode<T>> stationsToAvoid) {
        if (partialPaths.isEmpty()) return null;
        ArrayList<GraphNode<T>> currentPath = partialPaths.remove();
        GraphNode<T> currentNode = currentPath.get(0);
        if (currentNode.getValue().equals(destination.getValue())) return currentPath;
        if (encountered == null) encountered = new ArrayList<>();
        encountered.add(currentNode);
        for (GraphNode<T> adjNode : currentNode.getAdjacencyList().keySet())
            if (!encountered.contains(adjNode)) {
                if(!stationsToAvoid.contains(adjNode)) {
                    ArrayList<GraphNode<T>> newPath = new ArrayList<>(currentPath);
                    newPath.add(0, adjNode);
                    partialPaths.add(newPath);
                }
            }
        return shortestPathByNodes(partialPaths, encountered, destination, stationsToAvoid);
    }

    /* Interfacing method for DFS */
    public ArrayList<ArrayList<GraphNode<T>>> allPathsBetweenNodes(GraphNode<T> source, GraphNode<T> destination, ArrayList<GraphNode<T>> waypointStations, HashSet<GraphNode<T>> stationsToAvoid){
        ArrayList<ArrayList<GraphNode<T>>> routes = new ArrayList<>();
        ArrayList<GraphNode<T>> waypoints = new ArrayList<>(waypointStations);
        while (!waypoints.isEmpty()) {
            GraphNode<T> currentDestination = waypoints.remove(0);
            ArrayList<ArrayList<GraphNode<T>>> pathSection = allPathsBetweenNodes(source, null, currentDestination, stationsToAvoid);
            if (pathSection == null) return null;
            ArrayList<GraphNode<T>> tempPath = pathSection.get(0); // Need any one of paths to get the last node
            source = tempPath.get(tempPath.size() - 1);
            routes = joinRoutes(routes, pathSection);
        }
        ArrayList<ArrayList<GraphNode<T>>> pathSection = allPathsBetweenNodes(source, null, destination, stationsToAvoid);
        if (pathSection == null) return null;
        routes = joinRoutes(routes, pathSection);
        return routes;
    }

    private ArrayList<ArrayList<GraphNode<T>>> joinRoutes(ArrayList<ArrayList<GraphNode<T>>> routes1, ArrayList<ArrayList<GraphNode<T>>> routes2) {
        if (routes1.isEmpty()) return routes2;
        ArrayList<ArrayList<GraphNode<T>>> newRoutes = new ArrayList<>();
        for (ArrayList<GraphNode<T>> route1 : routes1) {
            for (ArrayList<GraphNode<T>> route2 : routes2) {
                ArrayList<GraphNode<T>> newRoute = new ArrayList<>();
                newRoute.addAll(route1);
                newRoute.addAll(route2);
                newRoute.remove(route1.size() - 1);
                newRoutes.add(newRoute);
            }
        }
        return newRoutes;
    }

    /* DFS */
    public ArrayList<ArrayList<GraphNode<T>>> allPathsBetweenNodes(GraphNode<T> source, ArrayList<GraphNode<T>> encountered, GraphNode<T> destination, HashSet<GraphNode<T>> stationsToAvoid) {
        ArrayList<ArrayList<GraphNode<T>>> result = null, pathsBetweenNodes;
        if (source.getValue().equals(destination.getValue())) {
            ArrayList<GraphNode<T>> newSolutionPath = new ArrayList<>();
            newSolutionPath.add(source);
            result = new ArrayList<>();
            result.add(newSolutionPath);
            return result;
        }
        if (encountered == null) encountered = new ArrayList<>();
        encountered.add(source);
        for(GraphNode<T> adjNode : source.getAdjacencyList().keySet()){
            if (!encountered.contains(adjNode) && !stationsToAvoid.contains(adjNode)) {
                pathsBetweenNodes = allPathsBetweenNodes(adjNode, new ArrayList<>(encountered), destination, stationsToAvoid);
                if (pathsBetweenNodes != null) {
                    for (ArrayList<GraphNode<T>> path : pathsBetweenNodes)
                        path.add(0, source);
                    if (result == null) result = pathsBetweenNodes;
                    else result.addAll(pathsBetweenNodes);
                }
            }
        }
        return result;
    }

    /* Interfacing method for Dijkstra's (order of waypoints matters) */
    public ArrayList<GraphNode<Station>> shortestPathBetweenStationsWithOrder(GraphNode<Station> source, GraphNode<Station> destination, double laneChangePenalty, ArrayList<GraphNode<Station>> waypointStations, HashSet<GraphNode<Station>> stationsToAvoid) {
        ArrayList<GraphNode<Station>> route = new ArrayList<>();
        ArrayList<GraphNode<Station>> currentRoute;
        ArrayList<GraphNode<Station>> waypoints = new ArrayList<>(waypointStations);
        while (!waypoints.isEmpty()) {
            currentRoute = shortestPathBetweenStationsWithOrder(source, waypoints.remove(0), laneChangePenalty, stationsToAvoid);
            if (currentRoute == null) return null;
            source = currentRoute.remove(currentRoute.size() - 1);
            route.addAll(currentRoute);
        }
        currentRoute = shortestPathBetweenStationsWithOrder(source, destination, laneChangePenalty, stationsToAvoid);
        if (currentRoute == null) return null;
        route.addAll(currentRoute);
        return route;
    }

    /* Dijkstra's with order for waypoints */
    public ArrayList<GraphNode<Station>> shortestPathBetweenStationsWithOrder(GraphNode<Station> source, GraphNode<Station> destination, double laneChangePenalty, HashSet<GraphNode<Station>> stationsToAvoid) {
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
        for (GraphNode<Station> adjNode : considered) {
            adjNode.setNodeValue(Double.MAX_VALUE);
            adjNode.setReturnNode(null);
        }
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
