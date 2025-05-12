import applied_computing.setu.Graph;
import applied_computing.setu.GraphNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private Graph<String> graph;
    private GraphNode<String> A, B, C, D, E;

    @BeforeEach
    public void setUp() {
        graph = new Graph<>();
        A = new GraphNode<>("A");
        B = new GraphNode<>("B");
        C = new GraphNode<>("C");
        D = new GraphNode<>("D");
        E = new GraphNode<>("E");

        A.connectNode(B, 1);
        B.connectNode(C, 1);
        C.connectNode(D, 1);
        D.connectNode(E, 1);
        B.connectNode(D, 2);
        A.connectNode(C, 4);
    }

    @Test
    public void testAllPathsBetweenNodes_DFS() {
        ArrayList<ArrayList<GraphNode<String>>> paths = graph.allPathsBetweenNodes(A, E, new ArrayList<>(), new HashSet<>());
        assertNotNull(paths);
        assertTrue(paths.size() >= 1);
        for (ArrayList<GraphNode<String>> path : paths) {
            assertEquals("A", path.get(0).getValue());
            assertEquals("E", path.get(path.size() - 1).getValue());
        }
    }

    @Test
    public void testShortestPathByWeight_Dijkstra() {
        ArrayList<GraphNode<String>> result = graph.shortestPathByWeight(A, E);
        assertNotNull(result);
        assertEquals("A", result.get(0).getValue());
        assertEquals("E", result.get(result.size() - 1).getValue());
    }

    @Test
    public void testShortestPathByNodes_BFS(){
        ArrayList<GraphNode<String>> result = graph.shortestPathByNodes(A, E, new HashSet<>());
        assertNotNull(result);
        assertEquals("A", result.get(0).getValue());
        assertEquals("E", result.get(result.size() - 1).getValue());
    }


}

