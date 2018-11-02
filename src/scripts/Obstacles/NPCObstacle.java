package scripts.Obstacles;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import scripts.Dialogue.Dialogue;
import scripts.Dialogue.DialogueUtils;
import scripts.Graph.Vertex;

import java.util.concurrent.Callable;

public class NPCObstacle extends Obstacle {

    private Dialogue dialogue;
    private int[] npcIDs;
    private String[] actions;
    private String[] names;

    public NPCObstacle(String id, Vertex vertex, Vertex goal, int[] npcIDs, String[] actions, String[] names) {
        super(id, vertex, goal);
        this.npcIDs = npcIDs;
        this.actions = actions;
        this.names = names;
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }

    @Override
    public boolean resolve(ClientContext ctx) {
        if(DialogueUtils.isChatting(ctx)) {
            DialogueUtils.resolve(ctx, dialogue);
        } else {

            Npc npc = ctx.npcs.select().id(this.npcIDs).nearest().poll();

            if (!npc.valid()) {
                System.out.println("FALLBACK, Attempting name object.");
                npc = ctx.npcs.select().name(names).nearest().within(vertex.tile(), 10).first().poll();
                if (!npc.valid()) return false;
            }

            if (npc.valid()) {
                if (!npc.inViewport()) {
                    ctx.movement.step(npc);
                    ctx.camera.turnTo(npc);
                } else {
                    if(npc.interact(ObstacleUtils.menuFilter(this.actions, this.names))) {
                        Condition.wait(ObstacleUtils.untillStill(ctx));
                        return true;
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
}
