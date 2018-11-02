package scripts.Parser;

import org.w3c.dom.Element;
import scripts.Dialogue.Dialogue;
import scripts.Graph.Graph;
import scripts.Graph.Vertex;
import scripts.Obstacles.*;

public class ObstacleParser {


    private static final String NAME = "name";
    private static final String TYPE = "type";

    private static final String NPC_ID = "npc_id";
    private static final String NPC_NAME = "npc_name";
    private static final String ITEM_ID = "item_id";

    private static final String BOUNDS = "bounds";
    private static final String GOAL_REACHABLE = "goal_reachable";
    private static final String OBJECT_ID = "object_id";
    private static final String OBJECT_NAME = "object_name";
    private static final String TRAPDOOR_OPEN_ID = "trapdoor_open_id";
    private static final String TRAPDOOR_CLOSED_ID = "trapdor_closed_id";

    private static final String ACTION = "action";
    private static final String DIALOGUE = "dialogue";

    private Graph graph;

    public ObstacleParser(Graph graph) {
        this.graph = graph;
    }

    public Obstacle loadObstacle(Element element, Vertex vertex, Vertex goal) {

        ObstacleType type = ObstacleType.parse(element.getAttribute(TYPE));
        Properties props = new Properties(element.getAttributes());

        String name = props.has(NAME) ? props.getString(NAME) : null;

        Obstacle obstacle;

        switch (type) {
            case OBJECT:
                obstacle = new ObjectObstacle(name, vertex, goal,
                        props.getInts(OBJECT_ID),
                        props.getStrings(ACTION),
                        props.getStrings(OBJECT_NAME));
                break;
            case NPC:
                obstacle = new NPCObstacle(name, vertex, goal,
                        props.getInts(NPC_ID),
                        props.getStrings(ACTION),
                        props.getStrings(NPC_NAME));

                break;
            case ITEM_SELECTION:
                obstacle = new ItemSelectionObstacle(name, vertex, goal,
                        props.getInts(ITEM_ID));
                break;
            default:
                throw new NullPointerException("Invalid type obstacle: " + props.toString());
        }

        //Add dialogue
        if(props.has(DIALOGUE)) {
            Dialogue dialogue = graph.dialogues().findByID(props.getString(DIALOGUE));
            obstacle.setDialogue(dialogue);
        }

        //Add bounds
        if(props.has(BOUNDS)) {
            int[] bounds = props.getBounds(BOUNDS);
            obstacle.setBounds(bounds);
        }

        //Add reachable
        if(props.has(GOAL_REACHABLE)) {
            obstacle.setGoalReachable(props.getBoolean(GOAL_REACHABLE));
        }

        return obstacle;
    }

}
