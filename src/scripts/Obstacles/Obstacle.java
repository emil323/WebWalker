package scripts.Obstacles;

import org.powerbot.script.rt4.ClientContext;
import scripts.Requirements.Requirement;
import scripts.Graph.Vertex;

import java.util.ArrayList;

/**
 * A obstacle is something that needs to be solved to continue, like opening a door, going up a ladder or paying for a boat trip
 */

public abstract class Obstacle {

     String id;
     Vertex vertex;
     Vertex goal;

     ArrayList<Requirement> requirements = new ArrayList<Requirement>();

    public Obstacle(String id, Vertex vertex, Vertex goal) {
        this.id = id;
        this.vertex = vertex;
        this.goal = goal;
    }

    /**
     * Checks if obstacle is in the way and need to be completed
     * @param next
     * @return
     */

    public boolean needToComplete(Vertex next) {
        return this.goal.equals(next);
    }

    /**
     * Contains the logic for completing a obstacle
     * @param ctx
     * @return
     */

    public abstract boolean resolve(ClientContext ctx);

    /**
     * Returns true if obstacle is solved, this can be overided for special requirements
     * @param ctx
     * @return
     */

    public  abstract boolean isSolved(ClientContext ctx);

    /**
     * Returns obstacleID
     * @return
     */

    public String getId() {
        return id;
    }

    /**
     * Returns vertex
     * @return
     */

    public Vertex getVertex() {
        return vertex;
    }

    /**
     * Set the requirements for this obstacle.
     * Like having high enough agility, or
     * @param requirements
     */

    public void setRequirements(ArrayList<Requirement> requirements) {
        this.requirements = requirements;
    }

    /**
     * Check if has requirements to contine, this is used in pathfinding.
     * The path will be ignored, if requirements are not met
     * @param ctx
     * @return
     */

    public boolean hasRequirements(ClientContext ctx) {

        boolean passed = true;
        for (Requirement req:this.requirements) {
            if(!req.hasRequirement(ctx)) {
                passed = false;
            }
        }
        return passed;
    }
}
