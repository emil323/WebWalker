package scripts.Obstacles;

import org.powerbot.script.rt4.ClientContext;
import scripts.Graph.Vertex;

public class WidgetObstacle extends Obstacle {
    public WidgetObstacle(String id, Vertex vertex, Vertex goal) {
        super(id, vertex, goal);
    }

    @Override
    public boolean resolve(ClientContext ctx) {
        return false;
    }

    @Override
    public boolean isSolved(ClientContext ctx) {
        return false;
    }
}
