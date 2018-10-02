package scripts.Obstacles;


import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

public class DialogueUtils {

    public DialogueUtils(){}


    public static void resolve(ClientContext ctx, Dialogue dialogue) {
        if(canContinue(ctx))
            continueChat(ctx);
        else {
            for (Component c : ctx.widgets.widget(219).component(1).components()) {
                System.out.println(c.text());
                if (dialogue.has(c.text())) {
                    c.click();
                    Condition.sleep(2000);
                }
            }
        }
    }

    public static void continueChat(ClientContext ctx){
        ctx.chat.clickContinue();
    }

    public  static boolean canContinue(ClientContext ctx) {
        return ctx.chat.canContinue();
    }

    public static boolean isChatting(ClientContext ctx) {
        return ctx.widgets.widget(231).componentCount() > 0 || ctx.widgets.widget(219).componentCount() > 0;
    }

}
