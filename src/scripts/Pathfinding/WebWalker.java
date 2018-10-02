package scripts.Pathfinding;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import scripts.Graph.Graph;
import scripts.Graph.GraphUtils;
import scripts.Vertex;

/**
 * This is the class you will normally interface with in your script
 */

public class WebWalker {

    private Graph graph;
    private ClientContext ctx;

    private Traverser traverser; //cache for traverser

    private Tile destination_tile = new Tile(0,0); //Define a destination tile

    public WebWalker(ClientContext ctx) {
        this.ctx = ctx;
    }

    /**
     * Loads the graph class,
     * this must be done in the start() in your script
     * @param folder
     * @return false if failed to load
     * TODO: Validation of XML files
     */

    public boolean loadGraph(String folder) {
        try {
            graph = GraphUtils.loadXML(folder +"graph.xml");
            GraphUtils.loadObstaclesXML(graph, folder +"surface.obstacles.xml");
            GraphUtils.loadObstaclesXML(graph, folder +"security_stronghold.obstacles.xml");
            traverser = new Traverser(ctx,graph);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Generated a path to "end_tile"
     * If the path generator already has generated a path to end_tile, it will return a cached traverser
     *
     * Example use in script:
     *  - webWalker.findPathTo(new Tile(3543,1234)).traverse()
     *
     * @param end_tile
     * @return Traverser
     */

    public Traverser findPathTo(Tile end_tile) {

        /*
            Check if traverser has path for
         */


        if(!this.traverser.isEmpty() && this.destination_tile.equals(end_tile)) {
            return this.traverser;
        }

        Vertex start = graph.getNearestReachableVertex(ctx);
        Vertex stop = graph.getNearestVertex(end_tile);

        PathGenerator generator = new PathGenerator(start, stop);
        Path path= generator.compute(ctx);
        //Add end_tile as it't own vertex
        path.add(new Vertex("end", end_tile, 3,false));
        this.destination_tile = end_tile;
        this.traverser.updatePath(path);
        return traverser;
    }

    /**
     * Generates the nearest path to bank, even if the bank is on another floor/plane
     *
     * @return Traverser
     */

    public Traverser findPathToBank() {

        //This is not foolproof, but relies on that player does not walk from bank to bank.
        //If using findPathTo, end() vertex will never be a bank because of end_tile is added as its own vertex.
        if(!this.traverser.isEmpty() && this.traverser.getPath().end().isBank()) {
            return this.traverser;
        }

        Vertex start = graph.getNearestReachableVertex(ctx);
        Vertex unknown_bank = new Vertex(null,null,-1,true);

        PathGenerator generator = new PathGenerator(start, unknown_bank);
        Path path= generator.compute(ctx);

        this.destination_tile = path.end().tile();
        this.traverser.updatePath(path);
        return traverser;
    }

    /**
     * Returns the graph object, can be useful if you want to customise vertices and making your own traverser
     * Also has methods getNearestReachableVertex(), and genNearestVertex() which is useful
     * @return graph
     */

    public Graph getGraph() {
        return graph;
    }

    /**
     * Returns fals if graph is not loaded,
     * Useful to use in repaint, if you want to draw paths on minimap.
     * @return boolean
     */

    public boolean isLoaded() {
        return this.graph != null;
    }
}
