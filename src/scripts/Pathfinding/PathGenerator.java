package scripts.Pathfinding;


import org.powerbot.script.rt4.ClientContext;
import scripts.Vertex;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Used to generate path from a start to stop
 *
 */

public class PathGenerator {

    private ClientContext ctx;

    private Vertex start;
    private Vertex stop;

    private boolean done = false;
    private boolean unknown_bank = false;

    private int counter = 0; //Counts how many iterations are done

    private static final int LIMIT = 100000; //Limit before giving up

    private ArrayList<Path> paths = new ArrayList<Path>(); //Arraylist of possible solutions
    private HashMap<String,Vertex> ignored = new HashMap(); //Hashmap of ignored vertices

    private Path solution = new Path(); //The best possible path, we define this to not return null

    /**
     * Supply start and stop vertex
     * @param start
     * @param stop
     */

    public PathGenerator(Vertex start, Vertex stop) {
        this.start = start;
        this.stop = stop;
    }

    /**
     * The inital method that starts the pathfinding
     * @param ctx
     * @return
     */

    public Path compute(ClientContext ctx) {
        this.ctx = ctx;

        Path originPath = new Path();

        originPath.add(this.start);
        paths.add(originPath);

        //Check if stop is bank, and set solution as nearest bank
        //Mechanism for computing path to nearest bank is pretty basic and versible
        //The stop Vertex needs to have a position as null, and having is_bank to true.
        if(this.stop.tile() == null && this.stop.isBank()) {
                this.unknown_bank = true;
        }

        //Do a while loop till we find out stop vertex, or the limit is met.
        while(!this.done && counter < LIMIT) this.computeNext();

        System.out.println("----------");
        System.out.println("Found after " + this.counter + " iterations.");
        System.out.println("Solution " + this.solution.toString());
        System.out.println("----------");

        return solution; //This can be null by design
    }

    /**
     * Iterate over paths and add possible paths to paths array
     */

    private void computeNext() {

        ArrayList<Path> newPaths = new ArrayList<Path>();

        for (Path path : this.paths) {

            //Check if goal is reached, if goal is without a position and bank.
            if(unknown_bank) {
                if (path.end().isBank()) {
                    this.done = true;
                    this.solution = path;
                }
            }
            if (path.end().equals(this.stop)) {
                this.done = true;
                this.solution = path;
            }

            //if path not stuck and not done, continue loop
            if (!path.stuck() && !this.done) {

                this.counter++; //Counter is incrementet for each loop in for loop.

                Vertex current = path.end();
                Vertex next = null;
                //Loop over a vertex's edge to find it's edges
                for (Vertex edge : current.getEdges()) {
                    //Check if edge is not ignored against the ignored hashmap
                    if(!this.ignored.containsKey(edge.getId())) {
                        //Set next vertex to edge
                        if (next == null)
                            next = edge;
                        else {
                            //Vertex has more than 2 edges if this else is true
                            //Create a new Pathfinding array in paths, with the clone we made earlier.
                            Path newPath = path.clone();
                            newPath.add(edge);
                            //Check if edge has requirements to go through, if not, set stuck flag
                            if (!current.meetsObstacleRequirements(ctx, edge))
                                newPath.setStuck(true); //not met requirements, set new path as stuck
                            newPaths.add(newPath); //Add to newPaths array
                        }
                        //Add to ignore hashmap
                        this.ignored.put(edge.getId(), edge);
                    }
                }
                //Check if has next, and if we our player meets requirements
                if (next != null && current.meetsObstacleRequirements(ctx, next))
                    path.add(next);
                else
                    path.setStuck(true); //next is null, set stuck flag.

            }
        }
        //Add new paths to paths
        this.paths.addAll(newPaths);
    }

}
