package scripts.Pathfinding;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import scripts.Graph.Graph;
import scripts.Obstacles.Obstacle;
import scripts.Graph.Vertex;

import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * The Traverser does the actual walking in RuneScape
 */

public class Traverser {

    private ClientContext ctx;
    private Graph graph;
    private Path path = new Path();

    private Vertex previous;
    private Vertex current;
    private Vertex next;

    private boolean done = false;

    private int reset_counter = 0;
    private final static int RESET_TRESHOLD = 3;

    /**
     * Constructor without a path
     * @param ctx
     * @param graph
     */

    public Traverser(ClientContext ctx, Graph graph) {
        this.ctx = ctx;
        this.graph = graph;
        this.path = path;
    }

    /**
     * Constructor with a path
     * @param ctx
     * @param graph
     * @param path
     */

    public Traverser(ClientContext ctx, Graph graph, Path path) {
        this.ctx = ctx;
        this.graph = graph;
        this.path = path;
    }


    /**
     * Find the position in the path and walks to next
     * If a obstacle is in the way, it makes sure it is completed before moving on.
     * Failsafes is added to make sure it won't get stuck
     *
     */

    public void traverse() {
        //If has animation and is not in combat, return. As there is nothing we can do yet. (ex. going trough agility shortcut, or going up a ladder)
        //We don't want to return if the player is in combat, then it will stop each time something in the path attacks.
        if(ctx.players.local().animation() != -1 && !ctx.players.local().inCombat()) return;
        if(ctx.players.local().inMotion() && ctx.movement.destination().distanceTo(ctx.players.local().tile()) > 6) return;

        if(current != null && !done) {
            //In motion, and not close to position yet, return.
            //if(ctx.players.local().inMotion() && current.tile().distanceTo(ctx.players.local().tile()) > current.getLeeway()) return;


            if(this.reset_counter >= RESET_TRESHOLD) {
                //Reset counter is at treshold, attempt to force walk.
                //This might happen when a region isn't fully loaded
                //Alternativly it is because a obstacle is incorrectly set-up
                System.out.println("Attempting to forcewalk: " + current.getId());
                if (ctx.players.local().tile().distanceTo(current.tile()) > current.getLeeway()) {
                    ctx.movement.step(this.current.tile());
                } else if(next != null) {
                    ctx.movement.step(this.next.tile());
                }
                Condition.sleep(100);
                this.reset_counter = 0;
            }

            Obstacle obstacle = current.getObstacle();

            if (ctx.players.local().tile().distanceTo(current.tile()) < current.getLeeway()) {
                //Check if obstacle in the way
                //Trigger proceed when next vertex equals goal
                if (obstacle != null && obstacle.needToComplete(next) && !obstacle.isSolved(ctx)) {
                    //Attempt to tacle obstacle
                    obstacle.resolve(ctx);
                    current = null; //Reset current vertex as a saftey measure to see if we are on right track
                } else {
                    //Check if end is reached
                    if(current.equals(path.end()))
                        this.done = true;
                      else {
                        //Iterate path
                        previous = current;
                        current = next;
                        next = this.findNext(current);
                    }
                }
            } else {

                //Check if current vertex i reachacle, and does not have unsolved obstacle
                if(ctx.movement.reachable(ctx.players.local().tile(), this.current.tile())) {
                    System.out.println("Current: " + current.getId());
                    //See if it is possible to step ahed
                    //We wish to step as far as possible on minimap without sacrificing reliability
                    if(next != null && !current.hasObstacle()
                            && ctx.players.local().tile().distanceTo(next.tile()) < 20) {
                            ctx.movement.step(this.next.getRandomisedTile());
                            this.reset_counter = 0;
                            current = next;
                            next = this.findNext(current);
                    }
                    else {//Or walk to current
                        ctx.movement.step(this.current.getRandomisedTile());
                            //Conditional wait to player is in motion, to avoid missclicking
                            Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    return ctx.players.local().inMotion();
                                }
                            }, 200, 3);
                    }
                } else {

                    System.out.println("can't reach: " + current.getId());
                    //Can not reach current vertex, due to location change
                    System.out.println("current vertex unreachable, reset counter: " + this.reset_counter);
                    this.reset_counter++;
                    current = null;
                }
            }
        } else {
            //current not inialized, find current and next vertex in path
            current = this.nearestVertex();
            next = this.findNext(current);
        }
    }


    /**
     * Find the next vertex in path
     * @param current
     * @return
     */

    private Vertex findNext(Vertex current) {
        Vertex next = null;

        if(current == null) return null;

        Iterator<String> itr = this.path.getVertices().keySet().iterator();
        while (itr.hasNext()) {
            if(itr.next().equals(current.getId())) {
                if(itr.hasNext()) next = path.getByID(itr.next());
            }
        }
        return next;
    }


    /**
     * Find the nearest REACHABLE vertex in path
     * @return
     */

    private Vertex nearestVertex() {
        double closest = Double.MAX_VALUE;
        Vertex current = null;

        for (Vertex vertex:this.path.getVertices().values()) {
            if(vertex.tile().distanceTo(ctx.players.local().tile()) < 30) {
                if (ctx.movement.reachable(ctx.players.local().tile(), vertex.tile())) {
                    if (vertex.tile().distanceTo(ctx.players.local().tile()) < closest) {
                        current = vertex;
                        closest = vertex.tile().distanceTo(ctx.players.local().tile());
                    }
                }
            }
        }
        return current;
    }

    /**
     * Set a new path to traverser
     * @param path
     */

    public void updatePath(Path path) {
        this.path = path;
        this.current = null;
        this.done = false;
    }

    /**
     * Returns destination vertex
     * @return vertex
     */

    public Vertex end() {
        return this.path.end();
    }

    /**
     * @return graph
     */

    public Graph getGraph() {
        return graph;
    }

    /**
     *
     * @return path
     */

    public Path getPath() {
        return path;
    }

    /**
     * Get previous vertex
     * @return vertex
     */

    public Vertex getPreviousVertex() {
        return previous;
    }

    /**
     * Get current vertex
     * @return vertex
     */

    public Vertex getCurrentVertex() {
        return current;
    }

    /**
     * Next vertex
     * @return vertex
     */

    public Vertex getNextVertex() {
        return next;
    }

    /**
     * check if empty
     * @return boolean
     */

    public boolean isEmpty() {
        return this.path.isEmpty();
    }

    /**
     * Returns true if done
     * @return boolean
     */

    public boolean isDone() {
        return done;
    }
}
