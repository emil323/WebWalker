package scripts.Obstacles;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import scripts.Dialogue.Dialogue;
import scripts.Utils.DialogueUtils;
import scripts.Graph.Vertex;

import java.util.concurrent.Callable;

public class NPCObstacle extends Obstacle {

    private Dialogue dialogue;
    private int npcID;
    private String action;
    private String name;

    public NPCObstacle(String id, Vertex vertex, Vertex goal, int npcID, String action, String name) {
        super(id, vertex, goal);
        this.npcID = npcID;
        this.action = action;
        this.name = name;
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }

    @Override
    public boolean resolve(ClientContext ctx) {
        if(DialogueUtils.isChatting(ctx)) {
            DialogueUtils.resolve(ctx, dialogue);
        } else {

            Npc npc = ctx.npcs.select().id(this.npcID).nearest().poll();

            if (!npc.valid()) {
                System.out.println("FALLBACK, Attempting name object.");
                npc = ctx.npcs.select().name(name).nearest().within(vertex.tile(), 10).first().poll();
                if (!npc.valid()) return false;
            }

            if (npc.valid()) {
                if (!npc.inViewport()) {
                    ctx.movement.step(npc);
                    ctx.camera.turnTo(npc);
                } else {
                    npc.interact(this.action, this.name);
                    Condition.wait(new Callable<Boolean>() {
                        @Override
                        public Boolean call() throws Exception {
                            return !ctx.players.local().inMotion();
                        }
                    }, 1000, 5);
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
