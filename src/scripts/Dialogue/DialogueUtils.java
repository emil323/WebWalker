package scripts.Dialogue;

import org.powerbot.script.Condition;
import org.powerbot.script.rt4.ChatOption;
import org.powerbot.script.rt4.ClientContext;
import scripts.Dialogue.Dialogue;

public class DialogueUtils {

    public static void resolve(ClientContext ctx, Dialogue dialogue) {
        if(canContinue(ctx))
            continueChat(ctx);
        else if (dialogue != null){
            ChatOption option = ctx.chat.select().text(dialogue.options()).poll();

            if(option.valid()) {
                option.select();
                Condition.sleep(3000);
            }
        }
    }


    @Deprecated
    public static void continueChat(ClientContext ctx){
        ctx.chat.clickContinue();
    }

    @Deprecated
    public  static boolean canContinue(ClientContext ctx) {
        return ctx.chat.canContinue();
    }

    @Deprecated
    public static boolean isChatting(ClientContext ctx) {
        //return ctx.widgets.widget(231).componentCount() > 0 || ctx.widgets.widget(219).componentCount() > 0;
        return ctx.chat.chatting();
    }

}
