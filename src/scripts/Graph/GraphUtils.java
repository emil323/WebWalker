package scripts.Graph;

import org.powerbot.script.Tile;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import scripts.Obstacles.*;
import scripts.Vertex;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class GraphUtils {


    /**
     * Strings for graph.xml
     */

    private static final String VERTICES = "vertices";
    private static final String VERTEX = "vertex";
    private static final String COORDINATES = "coordinates";
    private static final String ID = "id";
    private static final String X = "x";
    private static final String Y = "y";
    private static final String FLOOR = "floor";
    private static final String LEEWAY = "leeway";
    private static final String EDGE = "edge";
    private static final String EDGES = "edges";
    private static final String IS_BANK = "is_bank";

    /**
     * strings for surface.obstacles.xml
     */
    private static final String NAME = "name";
    private static final String OBSTACLE = "obstacle";
    private static final String TYPE = "type";
    private static final String GOAL_ID = "goal_id";
    private static final String REQUIREMENT = "requirement";

    private static final String ITEM_ID = "item_id";
    private static final String STACKABLE = "stackacle";
    private static final String AMOUNT = "amount";
    private static final String BOUNDS = "bounds";
    private static final String GOAL_REACHABLE = "goal_reachable";
    private static final String OBJECT_ID = "object_id";
    private static final String OBJECT_NAME = "object_name";
    private static final String TRAPDOOR_OPEN_ID = "trapdoor_open_id";
    private static final String TRAPDOOR_CLOSED_ID = "trapdor_closed_id";

    private static final String ACTION = "action";
    private static final String SKIlL_ID = "skill_id";
    private static final String REQUIRED_LEVEL = "required_level";


    //Requirement types
    private static final String INVENTORY = "inventory";
    private static final String SKILL = "skill";

    //Obstacle types
    private static final String UNIVERSAL = "universal";
    private static final String DOOR = "door";
    private static final String GATE = "gate";
    private static final String STAIRCASE= "staircase";
    private static final String CAVE= "cave";
    private static final String TRAPDOOR= "trapdoor";
    private static final String LADDER= "ladder";
    private static final String SECURITY_STRONGHOLD_GATE= "security_stronghold_gate";

    //Booleans
    private static final String TRUE = "true";
    private static final String FALSE= "false";

    /**
     * Loads a Graph based on XML file
     * @param path
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */

    public static Graph loadXML(String path) throws IOException, ParserConfigurationException, SAXException {

        //Create graph object
        Graph graph = new Graph();

        //Parse XML document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File( path ));

        //Create a NodeList of vertices
        NodeList nodes = document.getElementsByTagName(VERTEX);

        /*
        Create vertices
         */

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if(node.getNodeType() == Node.ELEMENT_NODE) {

                //This is one vertex
                Element element = (Element) node;

                //Fetch id of vertex
                String id = element.getAttribute(ID);

                //Fetch coordinates
                NamedNodeMap coordinates = element.getElementsByTagName(COORDINATES).item(0).getAttributes();

                int x = Integer.parseInt(coordinates.getNamedItem(X).getNodeValue());
                int y = Integer.parseInt(coordinates.getNamedItem(Y).getNodeValue());
                int floor = Integer.parseInt(coordinates.getNamedItem(FLOOR).getNodeValue());
                int leeway = Integer.parseInt(coordinates.getNamedItem(LEEWAY).getNodeValue());
                boolean is_bank = Boolean.parseBoolean(coordinates.getNamedItem(IS_BANK).getNodeValue());

                //Create Vertex
                Vertex vertex = new Vertex(id, new Tile(x, y, floor),leeway, is_bank);
                graph.addNode(vertex);
            }
        }
         /*
         * Create edges
         */
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {

                Element element = (Element) node;

                //Fetch vertex from graph
                String id = element.getAttribute(ID);
                Vertex vertex = graph.findNode(id);

                NodeList edges = element.getElementsByTagName(EDGE);

                //Add edges
                for (int j = 0; j < edges.getLength(); j++) {
                    Element edgeElement = (Element) edges.item(j);
                    String edgeID = edgeElement.getAttribute(ID);
                    Vertex edge = graph.findNode(edgeID);
                    graph.addEdge(vertex,edge);
                }
            }
        }
        //Debug
        System.out.println(graph.toString());
        return graph;
    }


    public static void loadObstaclesXML(Graph graph, String path) throws ParserConfigurationException, IOException, SAXException {
        //Parse XML document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new File( path ));

        //Create a NodeList of vertices
        NodeList nodes = document.getElementsByTagName(OBSTACLE);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                //This is the obstacle data
                //First check what type of obstacle this is
                Element obstacleElement = (Element) node;
                String obstacle_type = obstacleElement.getAttribute(TYPE);

                //Fetch vertices to add obstacle to
                NodeList verticesElement = obstacleElement.getElementsByTagName(VERTEX);
                for (int j = 0; j < verticesElement.getLength(); j++) {
                    Element vertexElement = (Element) verticesElement.item(j);

                    String name = obstacleElement.getAttribute(NAME);
                    Vertex vertex = graph.findNode(vertexElement.getAttribute(ID));
                    Vertex goal = graph.findNode(vertexElement.getAttribute(GOAL_ID));

                    ArrayList<Requirement> requirements = new ArrayList<Requirement>();

                    /**
                     * Load requirements
                     */
                    NodeList obstacleRequirements = obstacleElement.getElementsByTagName(REQUIREMENT);

                    for (int k = 0; k < obstacleRequirements.getLength(); k++) {
                        Element requirementElement = (Element) obstacleRequirements.item(k);
                        String requirement_type = requirementElement.getAttribute(TYPE);


                        if(requirement_type.equals(INVENTORY)) {

                            int itemID = Integer.parseInt(requirementElement.getAttribute(ITEM_ID));

                            if(requirementElement.hasAttribute(AMOUNT)) {
                                boolean stackable = false;
                                if(requirementElement.hasAttribute(STACKABLE))
                                    stackable = Boolean.parseBoolean(requirementElement.getAttribute(STACKABLE));

                                //Inventory requirement with amount (stackable)
                                int amount = Integer.parseInt(requirementElement.getAttribute(AMOUNT));
                                requirements.add(new InventoryRequirement(itemID, amount,stackable));
                            } else {
                                //Inventory requirement, single item
                                requirements.add(new InventoryRequirement(itemID));
                            }
                        }
                        if(requirement_type.equals(SKILL)) {
                            int skillID = Integer.parseInt(requirementElement.getAttribute(SKIlL_ID));
                            int required_level = Integer.parseInt(requirementElement.getAttribute(REQUIRED_LEVEL));
                            SkillRequirement requirement = new SkillRequirement(skillID,required_level);
                            requirements.add(requirement);
                        }
                    }


                    /**
                     * Security stronghold
                     */
                    if(obstacle_type.equals(SECURITY_STRONGHOLD_GATE)) {
                        int objectId = Integer.parseInt(obstacleElement.getAttribute(OBJECT_ID));
                        String object_name  =obstacleElement.getAttribute(OBJECT_NAME);

                        SecurityStrongholdGate object = new SecurityStrongholdGate(name, vertex, goal, objectId,object_name);

                        if(obstacleElement.hasAttribute(BOUNDS)) {
                            int[] bounds = boundsFromString(obstacleElement.getAttribute(BOUNDS));
                            object.setBounds(bounds);
                        }

                        object.setRequirements(requirements);
                        vertex.setObstacle(object);
                    }

                    /**
                     * Obstacle is door
                     */

                    if(obstacle_type.equals(DOOR)) {
                        int objectId = Integer.parseInt(obstacleElement.getAttribute(OBJECT_ID));

                        UniversalObstacle object = new UniversalObstacle(name, vertex, goal, objectId,"Open","Door");

                        if(obstacleElement.hasAttribute(BOUNDS)) {
                            int[] bounds = boundsFromString(obstacleElement.getAttribute(BOUNDS));
                            object.setBounds(bounds);
                        }
                        object.setRequirements(requirements);
                        vertex.setObstacle(object);
                    }

                    /**
                     * Obstacle is a gate
                     */

                    if(obstacle_type.equals(GATE)) {
                        int objectId = Integer.parseInt(obstacleElement.getAttribute(OBJECT_ID));

                        String action = "Open";
                        if(obstacleElement.hasAttribute(ACTION)) {
                            action = obstacleElement.getAttribute(ACTION);
                        }

                        UniversalObstacle object = new UniversalObstacle(name, vertex, goal, objectId,action,"Gate");


                        if(obstacleElement.hasAttribute(GOAL_REACHABLE)) {
                            if(obstacleElement.getAttribute(GOAL_REACHABLE).equals("true")) {
                                object.setGoalReachable(true);
                            } else {
                                object.setGoalReachable(false);
                            }

                        }

                        if(obstacleElement.hasAttribute(BOUNDS)) {
                            int[] bounds = boundsFromString(obstacleElement.getAttribute(BOUNDS));
                            object.setBounds(bounds);
                        }
                        object.setRequirements(requirements);
                        vertex.setObstacle(object);
                    }
                    /**
                     * Obstacle is a staircase
                     */
                    System.out.println(name);
                    if(obstacle_type.equals(STAIRCASE)) {

                        int objectId = Integer.parseInt(obstacleElement.getAttribute(OBJECT_ID));
                        String action = obstacleElement.getAttribute(ACTION);
                        UniversalObstacle object = new UniversalObstacle(name, vertex, goal, objectId,action,"Staircase");

                        if(obstacleElement.hasAttribute(BOUNDS)) {
                            int[] bounds = boundsFromString(obstacleElement.getAttribute(BOUNDS));
                            object.setBounds(bounds);
                        }
                        object.setRequirements(requirements);
                        vertex.setObstacle(object);
                    }
                    /**
                     * Obstacle is tunnel/cave
                     */
                    if(obstacle_type.equals(UNIVERSAL)) {
                        int objectId = Integer.parseInt(obstacleElement.getAttribute(OBJECT_ID));
                        String action = obstacleElement.getAttribute(ACTION);
                        String object_name  =obstacleElement.getAttribute(OBJECT_NAME);

                        UniversalObstacle object = new UniversalObstacle(name, vertex, goal, objectId,action,object_name);

                        if(obstacleElement.hasAttribute(BOUNDS)) {
                            int[] bounds = boundsFromString(obstacleElement.getAttribute(BOUNDS));
                            object.setBounds(bounds);
                        }
                        if(obstacleElement.hasAttribute(GOAL_REACHABLE)) {
                            if(obstacleElement.getAttribute(GOAL_REACHABLE).equals("true")) {
                                object.setGoalReachable(true);
                            } else {
                                object.setGoalReachable(false);
                            }

                        }
                        object.setRequirements(requirements);
                        vertex.setObstacle(object);
                    }

                    if(obstacle_type.equals(TRAPDOOR)) {
                        int trapdoor_openID = Integer.parseInt(obstacleElement.getAttribute(TRAPDOOR_OPEN_ID));
                        int trapdoor_closedID = Integer.parseInt(obstacleElement.getAttribute(TRAPDOOR_CLOSED_ID));

                        TrapdoorObstacle trapdoor = new TrapdoorObstacle(name, vertex, goal, trapdoor_closedID, trapdoor_openID);

                        if(obstacleElement.hasAttribute(BOUNDS)) {
                            int[] bounds = boundsFromString(obstacleElement.getAttribute(BOUNDS));
                            trapdoor.setBounds(bounds);
                        }
                        trapdoor.setRequirements(requirements);
                        vertex.setObstacle(trapdoor);
                    }
                    /**
                     * Obstacle is ladder
                     */

                    if(obstacle_type.equals(LADDER)) {
                        int objectId = Integer.parseInt(obstacleElement.getAttribute(OBJECT_ID));
                        String object_name = "Ladder";
                        String action = obstacleElement.getAttribute(ACTION);

                        if(obstacleElement.hasAttribute(OBJECT_NAME)) {
                            object_name = obstacleElement.getAttribute(OBJECT_NAME);
                        }

                        UniversalObstacle object = new UniversalObstacle(name, vertex, goal, objectId,action,object_name);

                        if(obstacleElement.hasAttribute(BOUNDS)) {
                            int[] bounds = boundsFromString(obstacleElement.getAttribute(BOUNDS));
                            object.setBounds(bounds);
                        }
                        object.setRequirements(requirements);
                        vertex.setObstacle(object);
                    }
                }
            }
        }
    }

    public static void createXML(Graph graph, String path) throws ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

        // root element
        Document document = docBuilder.newDocument();
        Element root = document.createElement(VERTICES);
        document.appendChild(root);

        Iterator<Vertex> iterator = graph.iterator();
        while (iterator.hasNext()) {
            Vertex vertex = iterator.next();
            //Create vertex element
            Element vertexElement = document.createElement(VERTEX);
            //append to root
            root.appendChild(vertexElement);
            //Create id attribute for vertex
            Attr attr = document.createAttribute(ID);
            attr.setValue(vertex.getId());
            vertexElement.setAttributeNode(attr);
            //Create coordinates
            Element coordinates = document.createElement(COORDINATES);
            vertexElement.appendChild(coordinates);

            Tile tile = vertex.tile();

            //Set attributes
            Attr x = document.createAttribute(X);
            Attr y = document.createAttribute(Y);
            Attr floor = document.createAttribute(FLOOR);
            Attr leeway = document.createAttribute(LEEWAY);
            Attr is_bank = document.createAttribute(IS_BANK);

            x.setValue(Integer.toString(tile.x()));
            y.setValue(Integer.toString(tile.y()));
            floor.setValue(Integer.toString(tile.floor()));
            leeway.setValue(Integer.toString(vertex.getLeeway()));
            is_bank.setValue(Boolean.toString(vertex.isBank()));

            coordinates.setAttributeNode(x);
            coordinates.setAttributeNode(y);
            coordinates.setAttributeNode(floor);
            coordinates.setAttributeNode(leeway);
            coordinates.setAttributeNode(is_bank);

            //Add edges
            Element edges = document.createElement(EDGES);
            vertexElement.appendChild(edges);

            for(Vertex edge: vertex.getEdges()) {
                Element edgeElement = document.createElement(EDGE);
                //Create id attribute for edge
                Attr edgeID = document.createAttribute(ID);
                edgeID.setValue(edge.getId());
                edgeElement.setAttributeNode(edgeID);

                edges.appendChild(edgeElement);
            }
        }

        //Save to XML file
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(new File(path));

        // Output to console for testing
        // StreamResult result = new StreamResult(System.out);

        transformer.transform(source, result);

        System.out.println("File saved!");
    }

    private static int[] boundsFromString(String text) {
        text = text.replaceAll("\\s",""); //Remove whitespace
        String[] split = text.split(","); //Split string by comma
        int len = split.length;
        int [] bounds = new int[len];
        for (int i = 0; i < len; i++) {
            bounds[i] = Integer.parseInt(split[i]);
        }
        return bounds;
    }

}
