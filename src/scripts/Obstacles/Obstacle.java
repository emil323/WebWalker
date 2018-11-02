package scripts.Obstacles;

import org.powerbot.script.rt4.ClientContext;
import scripts.Dialogue.Dialogue;
import scripts.Requirements.Requirement;
import scripts.Graph.Vertex;
import scripts.Requirements.RequirementStack;

import java.util.ArrayList;

/**
 * A obstacle is something that needs to be solved to continue, like opening a door, going up a ladder or paying for a boat trip
 */

public abstract class Obstacle {

    String id;
    Vertex vertex;
    Vertex goal;

    Obstacle next;

    boolean goal_reachable = false;

    Dialogue dialogue;
    int[] bounds;

    RequirementStack requirements = new RequirementStack();

    /**
     * Normal constructor
     * @param id
     * @param vertex
     * @param goal
     */

    public Obstacle(String id, Vertex vertex, Vertex goal) {
        this.id = id;
        this.vertex = vertex;
        this.goal = goal;
    }

    /**
     * Constructor where next obstacle is present
     * @param id
     * @param vertex
     * @param goal
     * @param next
     */

    public Obstacle(String id, Vertex vertex, Vertex goal, Obstacle next) {
        this.id = id;
        this.vertex = vertex;
        this.goal = goal;
        this.next = next;
    }

    /**
     * Checks if obstacle is in the way and need to be completed
     *
     * @param next
     * @return
     */

    public boolean needToComplete(Vertex next) {
        return this.goal.equals(next);
    }


    /**
     * Returns true if obstacle is solved, this should be overrided for special occations
     *
     * @param ctx
     * @return
     */

    public boolean isSolved(ClientContext ctx) {
        if(this.goal_reachable)
            return ctx.movement.reachable(ctx.players.local().tile(), this.goal.tile())
                    && ctx.players.local().tile().distanceTo(goal.tile()) < goal.getLeeway();
         else
            return ctx.movement.reachable(ctx.players.local().tile(), this.goal.tile());
    }


    /**
     * Returns true if has next obstacle
     *
     * @return
     */

    public boolean hasNext() {
        return this.next != null;
    }

    /**
     * Returns true if has dialogue
     *
     * @return
     */

    public boolean hasDialogue() {
        return this.dialogue != null;
    }
    /**
     * Returns true if has dialogue
     *
     * @return
     */


    public void setNext(Obstacle next) {
        this.next = next;
    }

    public boolean hasBounds() {
        return this.bounds != null;
    }


    public void setBounds(int [] bounds) {
        this.bounds = bounds;
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }

    public void setGoalReachable(boolean reachable) {
        this.goal_reachable = reachable;
    }

    public abstract boolean resolve(ClientContext ctx);


    /**
     * Returns obstacleID
     *
     * @return
     */

    public String getId() {
        return id;
    }

    /**
     * Returns vertex
     *
     * @return
     */

    public Vertex getVertex() {
        return vertex;
    }

    /**
     * Set the requirements for this obstacle.
     * Like having high enough agility, or
     *
     * @param requirements
     */

    public void setRequirementStack(RequirementStack requirements) {
        this.requirements = requirements;
    }

    /**
     * Requirement stack
     *
     * @return requirement stack
     */

    public RequirementStack requirements() {
        return this.requirements;
    }
}
