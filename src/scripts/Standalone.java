package scripts;


import scripts.Graph.Graph;
import scripts.Utils.GraphUtils;

/**
 * This class is useful for testing outside of RSBot
 */

public class Standalone {

    public static void main(String[] args) {
        Graph graph = null;
        try {
            graph = GraphUtils.loadGraphXML("C:\\Users\\scripts\\Rsbot_walkweb\\graph.xml");
            //GraphUtils.loadObstaclesXML(graph, "C:\\Users\\scripts\\Rsbot_walkweb\\surface.obstacles.xml");
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
