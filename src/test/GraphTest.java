import applied_computing.setu.Graph;
import applied_computing.setu.GraphNode;
import applied_computing.setu.Station;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private Graph<Station> graph;
    private GraphNode<Station> A, B, C, D, E;
    ArrayList<GraphNode<Station>> waypoints;
    HashSet<GraphNode<Station>> avoid;

    @BeforeEach
    public void setUp() {
        graph = new Graph<>();
        A = new GraphNode<>(new Station("A", new byte[]{1,2}));
        B = new GraphNode<>(new Station("B", new byte[]{1,3}));
        C = new GraphNode<>(new Station("C", new byte[]{1,2}));
        D = new GraphNode<>(new Station("D", new byte[]{1,3}));
        E = new GraphNode<>(new Station("E", new byte[]{1}));

        A.connectNode(B, 1);
        B.connectNode(C, 1);
        C.connectNode(D, 1);
        D.connectNode(E, 1);
        B.connectNode(D, 4);
        A.connectNode(C, 2);

        waypoints = new ArrayList<>();
        waypoints.add(E);
        waypoints.add(B);

        avoid = new HashSet<>();
        avoid.add(C);
    }

    //Basic

    @Test
    public void testAllPathsBetweenNodes_DFS() {
        ArrayList<ArrayList<GraphNode<Station>>> paths = graph.allPathsBetweenNodes(A, E, new ArrayList<>(), new HashSet<>());
        assertNotNull(paths);
        assertFalse(paths.isEmpty());
        assertEquals(4, paths.size());
        for (ArrayList<GraphNode<Station>> path : paths) {
            assertEquals("A", path.get(0).getValue().getName());
            assertEquals("E", path.get(path.size() - 1).getValue().getName());
            for(GraphNode<Station> node1 : path) {
                int count = 0;
                for(GraphNode<Station> node2 : path) {
                    if(node1 == node2) count++;
                }
                assertEquals(1, count);
            }

        }
    }

    @Test
    public void testShortestPathByWeight_Dijkstra() {
        ArrayList<GraphNode<Station>> result = graph.shortestPathBetweenStationsWithOrder(A, E, 0, new ArrayList<>(), new HashSet<>());
        assertNotNull(result);
        assertEquals("A", result.get(0).getValue().getName());
        assertEquals("E", result.get(result.size() - 1).getValue().getName());
        assertEquals(4, result.size());
    }

    @Test
    public void testShortestPathByNodes_BFS(){
        ArrayList<GraphNode<Station>> result = graph.shortestPathByNodes(A, E, new HashSet<>());
        assertNotNull(result);
        assertEquals("A", result.get(0).getValue().getName());
        assertEquals("E", result.get(result.size() - 1).getValue().getName());
        assertEquals(4, result.size());
    }

    //Advanced

    //Causing Errors
    @Test
    public void testShortestPathByWeight_Dijkstra_Waypoint() {
        ArrayList<GraphNode<Station>> result = graph.shortestPathBetweenStationsWithOrder(A, E, 0, waypoints, new HashSet<>());
        assertNotNull(result);
        assertEquals("A", result.get(0).getValue().getName());
        assertEquals("E", result.get(3).getValue().getName());
        assertEquals("B", result.get(6).getValue().getName());
        assertEquals("E", result.get(result.size() - 1).getValue().getName());
        assertEquals(10, result.size());
    }

    @Test
    public void testAllPathsBetweenNodes_DFS_Waypoint(){
        ArrayList<ArrayList<GraphNode<Station>>> paths = graph.allPathsBetweenNodes(A,E,waypoints, new HashSet<>());
        assertNotNull(paths);
        assertFalse(paths.isEmpty());
        for (ArrayList<GraphNode<Station>> path : paths) {
            assertEquals("A", path.get(0).getValue().getName());
            assertEquals("E", path.get(path.size() - 1).getValue().getName());
            int count_E = 0;
            int count_B = 0;
            for(GraphNode<Station> node1 : path) {
                if(node1 == E) count_E++;
                else if(node1 == B) count_B++;
            }
            assertEquals(2, count_E);
            assertTrue(count_B >=1);
        }
        assertEquals(36, paths.size());
    }

    @Test
    public void testShortestPathByNodes_BFS_Waypoint(){
        ArrayList<GraphNode<Station>> result = graph.shortestPathByNodes(A,E,waypoints, new HashSet<>());
        assertNotNull(result);
        assertEquals("A", result.get(0).getValue().getName());
        assertEquals("E", result.get(3).getValue().getName());
        assertEquals("B", result.get(5).getValue().getName());
        assertEquals("E", result.get(result.size() - 1).getValue().getName());
        assertEquals(8, result.size());
    }

    @Test
    public void testShortestPathByWeight_Dijkstras_Avoid(){
        ArrayList<GraphNode<Station>> result = graph.shortestPathBetweenStationsWithOrder(A, E, 0, new ArrayList<>(), avoid);
        assertNotNull(result);
        assertEquals("A", result.get(0).getValue().getName());
        assertEquals("E", result.get(result.size() - 1).getValue().getName());
        assertFalse(result.contains(C));
        assertEquals(4, result.size());
    }

    @Test
    public void testAllPathsBetweenNodes_DFS_Avoid(){
        ArrayList<ArrayList<GraphNode<Station>>> paths = graph.allPathsBetweenNodes(A,E, new ArrayList<>(), avoid);
        assertNotNull(paths);
        assertFalse(paths.isEmpty());
        assertEquals(1, paths.size());
        assertFalse(paths.get(0).contains(C));
    }

    @Test
    public void testShortestPathByNodes_BFS_Avoid(){
        ArrayList<GraphNode<Station>> result = graph.shortestPathByNodes(A,E, new ArrayList<>(), avoid);
        assertNotNull(result);
        assertEquals(4, result.size());
        assertFalse(result.contains(C));
    }



}

