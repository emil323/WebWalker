package scripts.Obstacles;

import org.powerbot.script.rt4.ClientContext;
import scripts.Graph.Vertex;

public class ItemOnObjectObstacle extends Obstacle {

    private int object_id;
    private int item_id;
    private String action;
    private String object_name;

    public ItemOnObjectObstacle(String id, Vertex vertex, Vertex goal, int object_id, int item_id, String action, String object_name) {
        super(id, vertex, goal);
        this.object_id = object_id;
        this.item_id = item_id;
        this.action = action;
        this.object_name = object_name;
    }

    @Override
    public boolean resolve(ClientContext ctx) {

        UniversalObstacle object = new UniversalObstacle(id, vertex, goal, object_id,action,object_name);

        if(ctx.inventory.selectedItem().id() != this.item_id)
            ctx.inventory.select().id(this.item_id).poll().click();
         else
            object.resolve(ctx);
        return false;
    }

    @Override
    public boolean isSolved(ClientContext ctx) {
        return ctx.movement.reachable(ctx.players.local().tile(), this.goal.tile());
    }
}
