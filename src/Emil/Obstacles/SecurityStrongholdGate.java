package Emil.Obstacles;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.*;
import Emil.Vertex;

import java.util.concurrent.Callable;

public class SecurityStrongholdGate extends Obstacle {

    private int object_id;
    private String object_name;
    private int[] bounds;
    private final String[] dialogues = {"Use the Account Recovery System.",
                                        "Don't share your information and report the player.",
                                        "Inform Jagex by emailing reportphishing@jagex.com.",
                                        "don't tell them anything and click the 'report abuse' button.",
                                        "Don't give them the information and send an 'Abuse report'.",
                                        "Set up 2 step authentication with my email provider.",
                                        "decline the offer and report the player",
                                        "no way! i'm reporting you to jagex!",
                                        "Me.","Nobody.", "No.",
                                        "report the incident and do not click any links",
                                        "do not visit the website and report the player who messaged you.",
                                        "don't type in my password backwards and report the player.",
                                        "secure my device and reset my runescape password.",
                                        "No, you should never buy a RuneScape account.",
                                        "Report the stream as a scam. Real Jagex streams have a 'verified' mark.",
                                        "No way! You'll just take my gold for your own! Reported!",
                                        "Read the text and follow the advice given.",
                                        "Authenticator and two-step login on my registered email.",
                                        "The birthday of a famous person or event.",
                                        "Report the incident and do not click any links.",
                                        "Politely tell them no and then use the 'Report Abuse' button.",
                                        "Decline the offer and report that player.",
                                        "Through account settings on runescape.com.",
                                        "Report the player for phishing.",
                                        "No, you should never allow anyone to level your account.",
                                        "Talk to any banker in RuneScape.",
                                        "Don't give out your password to anyone. Not even close friends.",
                                        "Don't click any links, forward the email to reportphishing@jagex.com.",
                                        "Virus scan my device then change my password.",
                                        "Don't give them my password.",
                                        "Only on the RuneScape website."};

    public SecurityStrongholdGate(String id, Vertex vertex, Vertex goal, int object_id, String object_name) {
        super(id, vertex, goal);
        this.object_id = object_id;
        this.object_name = object_name;
    }

    @Override
    public boolean resolve(ClientContext ctx) {
        System.out.println(this.getState(ctx));
        switch (this.getState(ctx)) {
            case DIALOUGE:
                if(ctx.chat.canContinue()) {
                    ctx.chat.clickContinue();
                    System.out.println("continue");
                } else {
                    for (Component c:ctx.widgets.widget(219).component(1).components()) {
                        System.out.println(c.text());
                        for (String d:this.dialogues) {
                            if(c.text().toLowerCase().equals(d.toLowerCase())) {
                                System.out.println("found");
                                c.click();
                                Condition.sleep(2000);
                            }
                        }
                    }

                }

                break;
            case OPEN_GATE:
                GameObject enter_object = ctx.objects.select().id(this.object_id).nearest(this.vertex.tile()).first().poll();
                if(enter_object.valid()) {
                    if(!enter_object.inViewport()) {
                        ctx.movement.step(enter_object);
                        ctx.camera.turnTo(enter_object);
                    } else {
                        if(bounds != null)
                            enter_object.bounds(bounds);

                        if(enter_object.interact("Open", this.object_name)) {
                            //Conditional wait for interaction to be done
                            Condition.wait(new Callable<Boolean>() {
                                @Override
                                public Boolean call() throws Exception {
                                    return isChatting(ctx);
                                }
                            }, 500, 5);
                        }
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public boolean isSolved(ClientContext ctx) {
        return ctx.movement.reachable(ctx.players.local().tile(), this.goal.tile());
    }

    private State getState(ClientContext ctx) {
        Player player = ctx.players.local();

        if(isChatting(ctx)) {
            return State.DIALOUGE;
        } else {
            if (ctx.movement.reachable(player.tile(), vertex.tile())) {
                return State.OPEN_GATE;
            } else {
                return State.WAIT;
            }
        }
    }

    public static boolean isChatting(ClientContext ctx){
       return ctx.widgets.widget(231).componentCount() > 0 || ctx.widgets.widget(219).componentCount() > 0;
    }

    private enum State {
        OPEN_GATE,WAIT,DIALOUGE
    }
    public void setBounds(int [] bounds) {
        this.bounds = bounds;
    }

}
