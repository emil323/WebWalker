package scripts.Pathfinding;


import org.powerbot.script.rt4.ClientContext;
import scripts.Graph.Vertex;
import scripts.Obstacles.Obstacle;

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
        int oldCounter = this.counter;

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

                int edgeCount = 0;
                final int FIRST = 0;

                //Loop over a vertex's edge to find it's edges
                for (Vertex edge : current.getEdges()) {
                    //Check if edge is not ignored against the ignored hashmap
                    if(!this.ignored.containsKey(edge.getId())) {
                        if(next == null)
                            next = edge;
                         else {
                            //Vertex has more than 2 edges if this else is true
                            //Create a new Pathfinding array in paths, with the clone we made earlier.
                            Path newPath = path.clone();
                            //Check if edge has requirements to go through, if not, set stuck flag
                            this.addToPath(newPath, edge);
                            newPaths.add(newPath); //Add to newPaths array
                        }
                        //Add to ignore hashmap
                        this.ignored.put(edge.getId(), edge);
                        edgeCount++;
                    }
                }

                if(next != null) {
                    this.addToPath(path, next);
                } else {
                    path.setStuck(true);
                }
            }
        }
        //Check if no progress was made, oldCounter == counter
        if(this.counter == oldCounter) {
            System.out.println("No path possible (probably).");
        }
        //Add new paths to paths
        this.paths.addAll(newPaths);
    }

    private void addToPath(Path path, Vertex next) {

        Vertex current = path.end();

        if(current.hasObstacle()) {
            Obstacle obstacle = current.getObstacle();
            if(!obstacle.requirementStack().isEmpty()) {
                if (obstacle.needToComplete(next)) {
                    System.out.println("need to complete obstacle at: " + next.getId());
                    if (obstacle.requirementStack().isMet(ctx, path.requirementStack())) {
                        //Add to path stack
                        path.requirementStack().addStack(obstacle.requirementStack());
                        System.out.println("Vertex: " + next.getId() + " Requirements met for obstacle: " + obstacle.toString());
                    } else {
                        System.out.println("Vertex: " + next.getId() + " Requirements not met for obstacle: " + obstacle.toString());
                        path.setStuck(true);
                    }
                }
            }
        }
        path.add(next);
    }

}
