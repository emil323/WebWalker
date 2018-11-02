package scripts.Obstacles;

import org.powerbot.script.rt4.ClientContext;
import scripts.Graph.Vertex;

public class ItemSelectionObstacle extends Obstacle {

    private int[] item_ids;

    public ItemSelectionObstacle(String id, Vertex vertex, Vertex goal,  int[] item_ids) {
        super(id, vertex, goal);
        this.item_ids = item_ids;
    }

    @Override
    public boolean resolve(ClientContext ctx) {
        for (int item_id:this.item_ids) {
            if(ctx.inventory.selectedItem().id() == item_id) {
                if(hasNext()) return next.resolve(ctx);
            }
        }
        return ctx.inventory.select().id(this.item_ids).poll().click();
    }


}
