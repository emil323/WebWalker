package scripts.Obstacles;


import org.powerbot.script.Filter;
import org.powerbot.script.MenuCommand;
import org.powerbot.script.rt4.ClientContext;

import java.util.concurrent.Callable;

public class ObstacleUtils {

    public static Filter<MenuCommand> menuFilter(String[] actions, String[] options) {
        return cmd -> {
            for (String action : actions) {
                for (String option : options) {
                    if (cmd.action.equals(action) && cmd.option.equals(option)) {
                        return true;
                    }
                }
            }
            return false;
        };
    }

    public static Callable<Boolean> untillStill(ClientContext ctx) {
        return () -> !ctx.players.local().inMotion() && ctx.players.local().animation() != -1;
    }

}
