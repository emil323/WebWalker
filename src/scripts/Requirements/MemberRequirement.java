package scripts.Requirements;

import org.powerbot.script.rt4.ClientContext;

public class MemberRequirement implements Requirement {
    @Override
    public boolean isMet(ClientContext ctx) {
        return ctx.client().isMembers();
    }

    @Override
    public String toString() {
        return "MemberRequirement";
    }
}
