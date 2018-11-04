package scripts.Obstacles;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;
import scripts.Dialogue.DialogueUtils;
import scripts.Graph.Vertex;

public class WidgetObstacle extends Obstacle {

    private int widget_id;
    private int [] components;
    private String text_click;

    public WidgetObstacle(String id, Vertex vertex, Vertex goal, int widget_id, int[] components, String text_click) {
        super(id, vertex, goal);
        this.widget_id = widget_id;
        this.components = components;
        this.text_click = text_click;
    }

    @Override
    public boolean resolve(ClientContext ctx) {
        for (Component c: ctx.widgets.widget(this.widget_id).components()) {
            if(c.text().equals(this.text_click)) {
                c.click();
                Condition.wait(ObstacleUtils.untillStill(ctx),100,10);
                return true;
            }
        }
        return false;
    }


}
