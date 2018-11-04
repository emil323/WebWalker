package scripts.Parser;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import scripts.Requirements.*;

public class RequirementParser {

    private static final String STACKABLE = "stackable";
    private static final String REMOVED = "removed";
    private static final String AMOUNT = "amount";
    private static final String TYPE = "type";
    private static final String ITEM_ID = "item_id";
    private static final String SKIlL_ID = "skill_id";
    private static final String REQUIRED_LEVEL = "required_level";
    private static final String QUEST_NAME = "quest-name";
    private static final String MEMBER = "member";


    public static RequirementStack parseRequirements(NodeList nodes) {

        RequirementStack requirements = new RequirementStack();

        for (int k = 0; k < nodes.getLength(); k++) {
            Element element = (Element) nodes.item(k);

            Properties props = new Properties(element.getAttributes());
            RequirementType type = RequirementType.parse(props.getString(TYPE));

            switch (type) {
                case INVENTORY:
                    requirements.add(new InventoryRequirement(
                            props.getInt(ITEM_ID),
                            props.getInt(AMOUNT),
                            props.getBoolean(STACKABLE),
                            props.getBoolean(REMOVED)
                    ));
                    break;
                case SKILL:
                    requirements.add(new SkillRequirement(
                            props.getInt(SKIlL_ID),
                            props.getInt(REQUIRED_LEVEL)
                    ));
                    break;
                case EQUIPMENT:
                    requirements.add(new EquipmentRequirement(
                            props.getInts(ITEM_ID)
                    ));
                    break;
                case QUEST:
                    requirements.add(new QuestRequirement(
                            props.getString(QUEST_NAME),
                            props.getBoolean(MEMBER)
                    ));
                    break;
                case MEMBER:
                    requirements.add(new MemberRequirement());
                    break;
            }

        }
        return requirements;
    }
}
