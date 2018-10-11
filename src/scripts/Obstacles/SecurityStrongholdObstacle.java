package scripts.Obstacles;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.*;
import scripts.Dialogue.Dialogue;
import scripts.Utils.DialogueUtils;
import scripts.Graph.Vertex;

import java.util.concurrent.Callable;

public class SecurityStrongholdObstacle extends Obstacle {

    private int object_id;
    private String object_name;
    private int[] bounds;
    private Dialogue dialogue;

    public SecurityStrongholdObstacle(String id, Vertex vertex, Vertex goal, int object_id, String object_name, Dialogue dialogue) {
        super(id, vertex, goal);
        this.object_id = object_id;
        this.object_name = object_name;
        this.dialogue = dialogue;
    }

    @Override
    public boolean resolve(ClientContext ctx) {

        if (DialogueUtils.isChatting(ctx)) {
            DialogueUtils.resolve(ctx, this.dialogue);
       /*
            for (Component c : ctx.widgets.widget(219).component(1).components()) {
                System.out.println(c.text());
                if (dialogue.has(c.text())) {
                    System.out.println("found");
                    c.click();
                    Condition.sleep(2000);
                }
            }
        */
        } else {
        GameObject enter_object = ctx.objects.select().id(this.object_id).nearest(this.vertex.tile()).first().poll();

        if(enter_object.valid()) {

            if (!enter_object.inViewport()) {
                ctx.movement.step(enter_object);
                ctx.camera.turnTo(enter_object);
            } else {
                if (bounds != null)
                    enter_object.bounds(bounds);

                if (enter_object.interact("Open", this.object_name)) {
                    //Conditional wait for interaction to be done
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return DialogueUtils.isChatting(ctx);
                        }
                    }, 500, 5);
                }
            }
        }
        }
        return false;
    }

    @Override
    public boolean isSolved(ClientContext ctx) {
        return ctx.movement.reachable(ctx.players.local().tile(), this.goal.tile());
    }



    public void setBounds(int [] bounds) {
        this.bounds = bounds;
    }

}
