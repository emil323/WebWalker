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

    private static final int LIMIT = 10000; //Limit before giving up

    private ArrayList<Path> paths = new ArrayList<Path>(); //Arraylist of possible solutions

    private HashMap<String,Vertex> ignoredVertices = new HashMap(); //Hashmap of ignored vertices
    private HashMap<String,Obstacle> ignoredObstacles = new HashMap(); //Hashmap of ignored obstacles


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

        resetPaths();

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
        System.out.println("Requirement stack: " + this.solution.requirementStack().toString());
        System.out.println("----------");

        return solution; //This can be null by design
    }

    /**
     * Iterate over paths and append possible paths to paths array
     */

    private void computeNext() {

        ArrayList<Path> newPaths = new ArrayList<Path>();
        boolean allPathsStuck = true;

        for (Path path : this.paths) {

            //Check if goal is reached, if goal is without a position and bank.

            if (unknown_bank) {
                if (path.end().isBank()) {
                    this.done = true;
                    this.solution = path;
                }
            }
            if (path.end().equals(this.stop) && !path.stuck()) {
                this.done = true;
                this.solution = path;
            }


            //if path not stuck and not done, continue loop
            if (!path.stuck() && !this.done) {
                allPathsStuck = false;
                this.counter++; //Counter is incrementet for each loop in for loop.

                Vertex current = path.end();
                Vertex next = null;

                int edgeCount = 0;
                final int FIRST = 0;

                //Loop over a vertex's edge to find it's edges
                for (Vertex edge : current.getEdges()) {
                    //Check if edge is not ignoredVertices against the ignoredVertices hashmap
                    if(!this.ignoredVertices.containsKey(edge.getId())) {
                        if(next == null)
                            next = edge;
                         else {
                            //Vertex has more than 2 edges if this else is true
                            //Create a new Pathfinding array in paths, with the clone we made earlier.
                            Path newPath = path.clone();
                            //Check if edge has requirements to go through, if not, set stuck flag

                            if(addRequirements(newPath,current,edge))
                                newPath.add(edge);
                            else
                                newPath.setStuck(true);
                            newPaths.add(newPath); //Add to newPaths array
                        }
                        //Add to ignore hashmap
                        this.ignoredVertices.put(edge.getId(), edge);
                        edgeCount++;
                    }
                }

                if(next != null && addRequirements(path, current, next))
                    path.add(next);
                 else
                    path.setStuck(true);
            }
        }
        //Check if no progress was made, oldCounter == counter
        if(allPathsStuck) {
            if(this.ignoredObstacles.isEmpty()) {
                System.out.println("No path found!");
                this.done = true;
            } else {
                System.out.println("Has obstacles previously successfull, ignore and try path");
                this.resetPaths();
                this.resetIgnoreObstacles();
            }

        }
        //Add new paths to paths
        this.paths.addAll(newPaths);
    }

    private boolean addRequirements(Path path, Vertex current, Vertex next) {

        if(current.hasObstacle()) {
            Obstacle obstacle = current.getObstacle();


            if(!obstacle.requirements().isEmpty()) {
                if (obstacle.needToComplete(next)) {
                    System.out.println("need to complete obstacle at: " + next.getId());
                        if (obstacle.requirements().isMet(ctx, path.requirementStack())) {
                            path.requirementStack().appendStack(obstacle.requirements());
                            this.ignoredObstacles.put(obstacle.getId(), obstacle);
                            //Add to path stack
                            System.out.println("Vertex: " + next.getId() + " Requirements met for obstacle: " + obstacle.toString());

                        } else {
                            System.out.println("Vertex: " + next.getId() + " Requirements not met for obstacle: " + obstacle.toString());
                            return false;
                        }
                    }
            }
        }
        return true;
    }

    private void resetIgnoreObstacles() {
        for(Obstacle o:this.ignoredObstacles.values()) {
            Vertex oVertex = o.getVertex();
            this.ignoredVertices.put(oVertex.getId(), oVertex);
            System.out.println("Ignored obstacle" + o.getId());
        }
        this.ignoredObstacles.clear();
    }

    private void resetPaths() {
        this.paths.clear();
        this.ignoredVertices.clear();
        Path originPath = new Path();
        originPath.add(this.start);
        this.paths.add(originPath);
    }

}
