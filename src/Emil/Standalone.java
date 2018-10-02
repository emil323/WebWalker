package Emil;


import Emil.Graph.Graph;
import Emil.Graph.GraphUtils;

/**
 * This class is useful for testing outside of RSBot
 */

public class Standalone {

    public static void main(String[] args) {
        Graph graph = null;
        try {
            graph = GraphUtils.loadXML("C:\\Users\\Emil\\Rsbot_walkweb\\graph.xml");
            GraphUtils.loadObstaclesXML(graph, "C:\\Users\\Emil\\Rsbot_walkweb\\surface.obstacles.xml");
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.print(graph.toString());



        //PathGenerator generator = new PathGenerator(graph.findNode("3a"), graph.findNode("4b"));
        //generator.compute();

        //DeveloperGUI gui = new DeveloperGUI();
        //gui.setNearestVertexID("test");
    }
}
