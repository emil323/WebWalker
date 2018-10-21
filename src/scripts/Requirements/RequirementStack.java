package scripts.Requirements;

import org.powerbot.script.rt4.ClientContext;
import java.util.ArrayList;

public class RequirementStack {

    private ArrayList<Requirement> requirements = new ArrayList<>();

    public RequirementStack() {
    }


    public void add(Requirement requirement) {
        this.requirements.add(requirement);
    }

    public void addStack(RequirementStack stack) {
        this.requirements.addAll(stack.getRequirements());
    }


    public ArrayList<Requirement> getRequirements() {
        return requirements;
    }

    public boolean isEmpty() {
        return requirements.isEmpty();
    }

    /**
     * Check if player meets requirements
     * @param ctx
     * @return
     */

    public boolean isMet(ClientContext ctx) {
        boolean passed = true;

        if(this.isEmpty()) return true;

        for(Requirement requirement: this.requirements)
            if(!requirement.isMet(ctx)) passed = false;
        return passed;
    }

    /**
     * Do isMet, but adding another stack to check (the other stack is probably a requirements stack from a path object)
     * @param ctx
     * @param otherStack
     * @return
     */

    public boolean isMet(ClientContext ctx, RequirementStack otherStack) {

        RequirementStack stack = new RequirementStack();

        stack.addStack(this);
        stack.addStack(otherStack);

        return stack.isMet(ctx);
    }

}
