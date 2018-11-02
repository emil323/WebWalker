package scripts.Obstacles;

import org.powerbot.script.Condition;
import org.powerbot.script.Filter;
import org.powerbot.script.MenuCommand;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import scripts.Dialogue.DialogueUtils;
import scripts.Graph.Vertex;

import java.util.Arrays;
import java.util.concurrent.Callable;

public class ObjectObstacle extends Obstacle {

    private int [] object_ids;
    private String[] actions;
    private String[] object_names;

    public ObjectObstacle(String id, Vertex vertex, Vertex goal, int [] object_ids, String [] actions, String [] object_names) {
        super(id, vertex, goal);
        this.object_ids = object_ids;
        this.actions = actions;
        this.object_names = object_names;
    }



    @Override
    public boolean resolve(final ClientContext ctx) {

        if(ctx.players.local().inMotion() || ctx.players.local().animation() != -1) return false;

        if(DialogueUtils.isChatting(ctx)) {
            DialogueUtils.resolve(ctx, dialogue);
        } else {
            GameObject object = ctx.objects.select().id(this.object_ids).nearest().within(vertex.tile(), 10).first().poll();

            if (!object.valid()) {
                System.out.println("FALLBACK, Attempting name object.");
                object = ctx.objects.select().name(object_names).nearest().within(vertex.tile(), 10).first().poll();
                if (!object.valid()) return false;
            }

            if (object.valid()) {
                if (!object.inViewport()) {
                    ctx.movement.step(object);
                    ctx.camera.turnTo(object);
                } else {
                    //Set bounds
                    if(this.hasBounds())
                        object.bounds(this.bounds);
                    //Interact
                    if(object.interact(ObstacleUtils.menuFilter(this.actions, this.object_names))) {
                        //Conditional wait for interaction to be done
                        Condition.wait(ObstacleUtils.untillStill(ctx));
                        return true;
                    }
                }
            }
        }
        return false;
    }




    @Override
    public String toString() {
        return "ObjectObstacle{" +
                "object_ids=" + object_ids +
                ", action='" + actions + '\'' +
                ", object_name='" + object_names + '\'' +
                ", bounds=" + Arrays.toString(bounds) +
                ", goal_reachable=" + goal_reachable +
                ", vertex=" + vertex.getId() +
                ", goal=" + goal.getId() +
                '}';
    }
}
