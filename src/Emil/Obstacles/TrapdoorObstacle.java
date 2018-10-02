package Emil.Obstacles;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.GameObject;
import Emil.Vertex;

public class TrapdoorObstacle extends Obstacle {

    private int trapdoor_closed_id;
    private int trapdoor_open_id;
    private int[] bounds;

    private final static String TRAPDOOR = "Trapdoor";
    private final static String CLIMB_DOWN = "Climb-down";
    private final static String OPEN = "Open";

    public TrapdoorObstacle(String id, Vertex vertex, Vertex goal, int trapdoor_closed_id, int trapdoor_open_id) {
        super(id, vertex, goal);
        this.trapdoor_closed_id = trapdoor_closed_id;
        this.trapdoor_open_id = trapdoor_open_id;
    }

    @Override
    public boolean resolve(ClientContext ctx) {
        GameObject trapdoor_closed = ctx.objects.select().id(this.trapdoor_closed_id).within(vertex.tile(), 10).first().poll();
        GameObject trapdoor_open = ctx.objects.select().id(this.trapdoor_open_id).within(vertex.tile(), 10).first().poll();

        if(bounds != null) {
            trapdoor_closed.bounds(this.bounds);
            trapdoor_open.bounds(this.bounds);
        }

        if(trapdoor_closed != null) {
            if(!trapdoor_closed.inViewport()) {
                ctx.movement.step(trapdoor_closed);
                ctx.camera.turnTo(trapdoor_closed);
            } else {
                System.out.println("Attempting to open trapdoor");
                trapdoor_closed.interact(OPEN, TRAPDOOR);
            }
        }
        if(trapdoor_open != null) {
            if(!trapdoor_open.inViewport()) {
                ctx.movement.step(trapdoor_open);
                ctx.camera.turnTo(trapdoor_open);
            } else {
                System.out.println("Attempting to climb down trapdoor");
                trapdoor_open.interact(CLIMB_DOWN, TRAPDOOR);
            }
        }
        return this.isSolved(ctx);
    }

    @Override
    public boolean isSolved(ClientContext ctx) {
        return ctx.movement.reachable(ctx.players.local().tile(), this.goal.tile());
    }

    public void setBounds(int [] bounds) {
        this.bounds = bounds;
    }

}
