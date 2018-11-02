package scripts.Parser;

import org.powerbot.script.Tile;
import org.w3c.dom.*;
import org.xml.sax.SAXException;
import scripts.Graph.Graph;
import scripts.Obstacles.*;
import scripts.Dialogue.Dialogue;
import scripts.Requirements.RequirementStack;
import scripts.Graph.Vertex;

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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.stream.Stream;

public class GraphParser {


    /**
     * Strings for graph
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
     * strings for obstacles
     */
    private static final String OBSTACLE = "obstacle";
    private static final String GOAL_ID = "goal_id";
    private static final String REQUIREMENT = "requirement";
    private static final String NEXT = "next";

    //Dialogue
    private static final String DIALOGUE = "dialogue";
    private static final String OPTION = "option";


    /**
     * Loads a Graph based on XML file
     * @param path
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */

    public static Graph loadGraphXML(String path) throws IOException, ParserConfigurationException, SAXException {

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


    public static void loadDialoguesXML(Graph graph, Path path)  {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = builder.parse(path.toFile());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create a NodeList of dialogues
        NodeList nodes = document.getElementsByTagName(DIALOGUE);

        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element dialogueElement = (Element) node;

                String id = dialogueElement.getAttribute(ID);

                NodeList optionsList =dialogueElement.getElementsByTagName(OPTION);

                String[] options = new String[optionsList.getLength()];

                for (int j = 0; j < optionsList.getLength(); j++) {
                    Element optionElement = (Element) optionsList.item(j);
                    options[j] = optionElement.getTextContent();
                }
                Dialogue dialogue = new Dialogue(id, options);
                graph.dialogues().addDialogue(dialogue);
            }
        }
    }

    public static void loadObstaclesXML(Graph graph, Path path)  {

        ObstacleParser obstacleParser = new ObstacleParser(graph);

        //Parse XML document
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document document = null;
        try {
            document = builder.parse(path.toFile());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Create a NodeList of vertices
        NodeList nodes = document.getElementsByTagName(OBSTACLE);
        for (int i = 0; i < nodes.getLength(); i++) {
            Node node = nodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                //This is the obstacle data
                //First check what type of obstacle this is
                Element obstacleElement = (Element) node;
                //Fetch vertices to append obstacle to
                NodeList verticesElement = obstacleElement.getElementsByTagName(VERTEX);
                for (int j = 0; j < verticesElement.getLength(); j++) {
                    Element vertexElement = (Element) verticesElement.item(j);

                    //load start and goal
                    Vertex start = graph.findNode(vertexElement.getAttribute(ID));
                    Vertex goal = graph.findNode(vertexElement.getAttribute(GOAL_ID));

                    //load obstacle element
                    Obstacle obstacle = obstacleParser.loadObstacle(obstacleElement, start, goal);

                    NodeList nextObstacleNode = obstacleElement.getElementsByTagName(NEXT);
                    if(nextObstacleNode.getLength() > 0 ) {
                        Obstacle next = obstacleParser.loadObstacle((Element) nextObstacleNode.item(0), start, goal);
                        obstacle.setNext(next);
                    }

                    //Load requirements
                    NodeList requirementsNodes = obstacleElement.getElementsByTagName(REQUIREMENT);
                    RequirementStack requirements = RequirementParser.parseRequirements(requirementsNodes);

                    //Load requirements stack, and set obstacle to start vertex
                    obstacle.setRequirementStack(requirements);
                    start.setObstacle(obstacle);
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


}
