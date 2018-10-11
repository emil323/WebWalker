package scripts.Obstacles;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import scripts.Dialogue.Dialogue;
import scripts.Utils.DialogueUtils;
import scripts.Graph.Vertex;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class UniversalObstacle extends Obstacle {

    private int object_id;
    private String action;
    private String object_name;

    private int[] bounds;
    private Dialogue dialogue;

    private boolean goal_reachable = false; //Set to true if goal tile is reachable

    public UniversalObstacle(String id, Vertex vertex, Vertex goal, int object_id, String action, String object_name) {
        super(id, vertex, goal);
        this.object_id = object_id;
        this.action = action;
        this.object_name = object_name;
    }



    @Override
    public boolean resolve(final ClientContext ctx) {

        if(ctx.players.local().inMotion() || ctx.players.local().animation() != -1) return false;

        if(DialogueUtils.isChatting(ctx)) {
            DialogueUtils.resolve(ctx, dialogue);
        } else {
            GameObject object = ctx.objects.select().id(this.object_id).nearest().within(vertex.tile(), 10).first().poll();

            if (!object.valid()) {
                System.out.println("FALLBACK, Attempting name object.");
                object = ctx.objects.select().name(object_name).nearest().within(vertex.tile(), 10).first().poll();
                if (!object.valid()) return false;
            }

            if (object.valid()) {
                if (!object.inViewport()) {
                    ctx.movement.step(object);
                    ctx.camera.turnTo(object);
                } else {
                    System.out.println("Attempting to " + this.action + " " + this.object_name);
                    if (bounds != null)
                        object.bounds(bounds);
                    //Interact
                    object.interact(this.action, this.object_name);
                    //Conditional wait for interaction to be done
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !ctx.players.local().inMotion() && ctx.players.local().animation() != -1;
                        }
                    }, 500, 5);
                }
            }
        }
        return this.isSolved(ctx);
    }

    public  boolean isSolved(ClientContext ctx) {
        if(this.goal_reachable) {
            System.out.println("Goal reachable");
            return ctx.movement.reachable(ctx.players.local().tile(), this.goal.tile())
                    && ctx.players.local().tile().distanceTo(goal.tile()) < goal.getLeeway();
        } else
            return ctx.movement.reachable(ctx.players.local().tile(), this.goal.tile());
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


    @Override
    public String toString() {
        return "UniversalObstacle{" +
                "object_id=" + object_id +
                ", action='" + action + '\'' +
                ", object_name='" + object_name + '\'' +
                ", bounds=" + Arrays.toString(bounds) +
                ", goal_reachable=" + goal_reachable +
                ", vertex=" + vertex.getId() +
                ", goal=" + goal.getId() +
                '}';
    }
}
