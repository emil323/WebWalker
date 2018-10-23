package scripts.Requirements;

import org.powerbot.script.rt4.ClientContext;

public class SkillRequirement implements Requirement {

    private int skill;
    private int required_level;

    public SkillRequirement(int skill, int required_level) {
        this.skill = skill;
        this.required_level = required_level;
    }

    public int getSkill() {
        return this.skill;
    }

    @Override
    public boolean isMet(ClientContext ctx) {
        return ctx.skills.level(this.skill) >= this.required_level;
    }

}
