package scripts.Requirements;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.rt4.Component;

public class QuestRequirement implements Requirement {

    private boolean members;
    private String quest_name;

    private final static int QUEST_WIDGET = 399;
    private final static int F2P_COMPONENT = 9;
    private final static int P2P_OOMPONENT = 10;

    private final static int GREEN = 901389;

    public QuestRequirement(String quest_name, boolean members) {
        this.quest_name = quest_name;
        this.members = members;
    }

    @Override
    public boolean isMet(ClientContext ctx) {
        //If quest is members, and player is not member, return false
        if (this.members && !ctx.client().isMembers()) return false;
        int componentID = this.members ? P2P_OOMPONENT : F2P_COMPONENT;
        //Loop trough components og component, and check if quest is highlighted green
        for (Component c : ctx.widgets.widget(QUEST_WIDGET).component(componentID).components()) {
            if (c.text().equals(this.quest_name)) {
                if (c.textColor() == GREEN) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "QuestRequirement{" +
                "members=" + members +
                ", quest_name='" + quest_name + '\'' +
                '}';
    }
}
