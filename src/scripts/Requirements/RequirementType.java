package scripts.Requirements;

public enum RequirementType {
    INVENTORY, SKILL, MEMBER, QUEST, EQUIPMENT;

    public static RequirementType parse(String s) {
        for(RequirementType r:RequirementType.values()){
            if(r.name().equalsIgnoreCase(s)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Obstacle type invalid: " + s);
    }
}
