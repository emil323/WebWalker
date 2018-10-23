package scripts.Requirements;

import org.powerbot.script.rt4.ClientContext;
import java.util.ArrayList;

public class RequirementStack {

    private ArrayList<Requirement> requirements = new ArrayList<>();

    public RequirementStack() {
    }

    public RequirementStack(ArrayList<Requirement> requirements ) {
        this.requirements = requirements;
    }


    public void add(Requirement requirement) {
        this.requirements.add(requirement);
    }

    /**
     * Check if requirement matches another requirement (is summable), if so appendValue
     * Else the requirement is added to the requirements ArrayList
     * @param newRequirement
     */

    public void append(Requirement newRequirement) {

        Requirement summedRequirement = null;

        for (Requirement currentRequirement:this.requirements) {
            if(currentRequirement.summable(newRequirement)) {
                summedRequirement = currentRequirement.sumClone(newRequirement);
                System.out.println("summable: " + newRequirement.toString());
            }
        }
        if(summedRequirement != null)
            this.requirements.add(summedRequirement);
        else
            this.requirements.add(newRequirement);
    }

    /**
     * Loop trough and do append() method for whole stack
     * @param stack
     */

    public void appendStack(RequirementStack stack) {
        for (Requirement r:stack.getRequirements())
            this.append(r);
    }


    public ArrayList<Requirement> getRequirements() {
        return this.requirements;
    }

    public RequirementStack clone() {
        return new RequirementStack((ArrayList<Requirement>)this.requirements.clone());
    }

    public boolean isEmpty() {
        return this.requirements.isEmpty();
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

        stack.appendStack(this.clone());
        stack.appendStack(otherStack.clone());
        System.out.println(stack.toString());
        return stack.isMet(ctx);
    }

    @Override
    public String toString() {
        String desc = "RequirementStack{ ";
        for(Requirement r:this.requirements)
            desc += r.toString();
               desc +=  '}';
        return desc;
    }
}
