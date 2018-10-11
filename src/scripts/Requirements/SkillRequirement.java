package scripts.Requirements;

import org.powerbot.script.rt4.ClientContext;

public class SkillRequirement implements Requirement {

    private int skill;
    private int required_level;

    public SkillRequirement(int skill, int required_level) {
        this.skill = skill;
        this.required_level = required_level;
    }

    @Override
    public boolean hasRequirement(ClientContext ctx) {
        return ctx.skills.level(this.skill) >= this.required_level;
    }
}
