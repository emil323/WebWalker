package Emil.SuperFighter;

import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Npc;
import Emil.Pathfinding.WebWalker;
import Emil.Vertex;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.Callable;

/**
 * Example script, will fight anywhere in RuneScape
 * modify food_id and monster string.
 */

@Script.Manifest(name="SuperFighter", description="Customisable autofighter script with webwalking, banks loot and eats.", properties = "author=Emil; client=4")
public class SuperFighter extends PollingScript<ClientContext> implements PaintListener {

    private WebWalker webWalker = new WebWalker(ctx);

    private Tile starting_point;

    private int food_id = 379;
    private String monster = "Jelly";

    /**
     * Load graph on start
     */

    @Override
    public void start() {
        super.start();
        if(webWalker.loadGraph("")) {
            System.out.println("webwalker loaded successfully.");
        } else {
            System.out.println("failed to load webwalker");
            ctx.controller.stop();
        }

        //Get starting point
        starting_point = ctx.players.local().tile();
    }

    @Override
    public void poll() {

        if(!ctx.movement.running()) {
            if(ctx.movement.energyLevel() > 30) {
                ctx.movement.running(true);
            }
        }

        switch (this.getState()) {
            case WALKING_TO_BANK:
                    webWalker.findPathToBank().traverse();
                break;
            case BANKING:
                if(ctx.players.local().inMotion()) break;
                if(ctx.bank.opened()) {
                    if(ctx.bank.select().id(this.food_id).isEmpty()) {
                        System.out.println("No more food left,");
                        ctx.controller.stop();
                    } else {
                        if (ctx.bank.withdraw(this.food_id, 27)) {
                            System.out.println("Withdrawed food.");
                            ctx.bank.close();
                        }
                    }
                } else {
                    ctx.bank.open();
                }
                break;
            case WALKING_TO_FIGHT:
                webWalker.findPathTo(this.starting_point).traverse();
                break;
            case FIGHTING:

                Npc npc = ctx.npcs.select(new Filter<Npc>() {
                    @Override
                    public boolean accept(Npc npc) {
                        return !npc.interacting().valid() && !npc.inCombat() && npc.healthPercent() > 0 && npc.animation() == -1 ;
                    }
                }).select().name(this.monster).nearest().poll();

                if(!ctx.players.local().interacting().valid() && !ctx.players.local().inCombat() && !ctx.players.local().inMotion()) {
                    if(npc != null && !npc.inCombat()) {
                        if(npc.inViewport()) {

                            npc.interact("Attack", this.monster);
                            Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    return (ctx.players.local().inCombat() || ctx.players.local().interacting().valid())
                                            && !ctx.players.local().inMotion();
                                }}, 500, 5);
                        } else {
                            System.out.println("not in viewport");
                        }
                    }
                } else
                if(ctx.players.local().healthPercent() < 50) {
                    ctx.inventory.select().id(this.food_id).poll().click();
                    Condition.sleep(1000);
                }

                break;
        }
    }


    /**
     * Get current state
     * @return
     */

    public States getState() {
        if(ctx.inventory.select().id(this.food_id).isEmpty()) {
            if(ctx.bank.inViewport()) {
                return States.BANKING;
            } else {
                return States.WALKING_TO_BANK;
            }
        } else {

            if(ctx.players.local().tile().distanceTo(this.starting_point) < 20) {
                return States.FIGHTING;
            } else {
                return States.WALKING_TO_FIGHT;
            }
        }
    }


    /**
     * Paints a path on minimap when walking
     * @param g
     */

    @Override
    public void repaint(Graphics g) {
        g.drawString(getState().toString(), 20, 20);
        States state = getState();
        if (webWalker.isLoaded()) {
            if (state.equals(States.WALKING_TO_BANK) || state.equals(States.WALKING_TO_FIGHT)) {
                Iterator<Vertex> itr = webWalker.getGraph().iterator();
                while (itr.hasNext()) {
                    Vertex vertex = itr.next();
                    Tile tile = vertex.tile();
                    //Only draw nearby vertices
                    if (ctx.players.local().tile().distanceTo(tile) < 25) {

                        Point mapPoint = tile.matrix(ctx).mapPoint();

                        //g.drawString( vertex.getId(), mapPoint.x, mapPoint.y);
                        g.setColor(Color.YELLOW);
                        g.clearRect(mapPoint.x, mapPoint.y, 5, 5);

                        //Draw lines between edges on minimap

                        ArrayList<Vertex> edges = vertex.getEdges();

                        for (Vertex edge : edges) {
                            if (edge.tile().distanceTo(vertex.tile()) < 40) {
                                //This will draw double, but does not matter.
                                Point p1 = edge.tile().matrix(ctx).mapPoint();
                                Point p2 = vertex.tile().matrix(ctx).mapPoint();
                                g.setColor(Color.CYAN);
                                g.drawLine(p1.x, p1.y, p2.x, p2.y);
                            }
                        }

                    }
                }
            }
        }
    }


}
