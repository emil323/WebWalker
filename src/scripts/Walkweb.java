package scripts;



import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import scripts.Graph.Graph;
import scripts.Graph.Vertex;
import scripts.Parser.GraphParser;
import scripts.Parser.ParserUtils;
import scripts.Pathfinding.Traverser;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * This script is made to test walkweb and a contains a GUI to create new vertices.
 */

@Script.Manifest(name="Walkweb", description="cuts willow logs and drops them", properties = "author=scripts;client=4")
public class Walkweb extends PollingScript<ClientContext> implements PaintListener{

    private Graph graph;
    private DeveloperGUI gui;
    private Developer dev;

    @Override
    public void start() {
        super.start();
        try {
            graph = GraphParser.loadGraphXML("data/graph.xml");

            ParserUtils.findFiles(".dialogues.xml").forEach(path -> {
                System.out.println(path);
                GraphParser.loadDialoguesXML(graph,path);
            });

            ParserUtils.findFiles(".obstacles.xml").forEach(path -> {
                System.out.println(path);
                GraphParser.loadObstaclesXML(graph,path);
            });


            //GraphParser.loadDialoguesXML(graph, "security_stronghold.dialogues.xml");
            //GraphParser.loadObstaclesXML(graph, "surface.obstacles.xml");
            //GraphParser.loadObstaclesXML(graph, "security_stronghold.obstacles.xml");
            System.out.println(graph.dialogues().toString());
        } catch (Exception e) {
            e.printStackTrace();
            ctx.controller.stop();
        }
        dev = new Developer(ctx, graph);
        gui = new DeveloperGUI(dev);
        System.out.print(graph.toString());
    }

    Traverser traverser;

    /**
     * The RSBot client will loop poll(), we need to take that into account when writing scripts and libraries for RSbot
     */

    @Override
    public void poll() {

        Vertex nearest = graph.getNearestVertex(ctx.players.local().tile());
        if(nearest != null) {
            gui.setNearestVertexID(nearest.getId());
        }
        if(dev.pathAvailable()) {
            if(traverser == null) {
                traverser = new Traverser(ctx, graph, dev.getPath());
            }
            traverser.traverse();

            //Reset traverser
            if(traverser.isDone()) {
                System.out.println("We are done!");
                traverser = null;
                dev.resetPath();
            }
            //TilePath tilePath = ctx.movement.newTilePath(dev.getPath().toTiles());
            //tilePath.traverse();
        }
    }


    /**
     * Example of drawing graph on minimap
     * @param g
     */

    @Override
    public void repaint(Graphics g) {

        if (graph != null) {
            try {
                Iterator<Vertex> itr = graph.iterator();
                while (itr.hasNext()) {
                    Vertex vertex = itr.next();
                    Tile tile = vertex.tile();
                    //Only draw nearby vertices
                    if (ctx.players.local().tile().distanceTo(tile) < 50) {
                        Polygon poly = tile.matrix(ctx).bounds();
                        Point point = tile.matrix(ctx).centerPoint();
                        Point mapPoint = tile.matrix(ctx).mapPoint();

                        //Draw rectangle on tile and point on minimap

                        g.setColor(Color.WHITE);
                        g.drawPolygon(poly);

                        if(vertex.isBank()) {
                            g.drawString(vertex.getId() + " (Bank)", point.x, point.y);
                        } else {
                            g.drawString(vertex.getId(), point.x, point.y);
                        }

                        //g.drawString( vertex.getId(), mapPoint.x, mapPoint.y);


                        //Draw lines between edges on minimap

                        ArrayList<Vertex> edges = vertex.getEdges();

                        for (Vertex edge : edges) {
                            //This will draw double, but does not matter.
                            if(edge.tile().distanceTo(vertex.tile()) < 30) {
                                Point p1 = edge.tile().matrix(ctx).mapPoint();
                                Point p2 = vertex.tile().matrix(ctx).mapPoint();
                                g.setColor(Color.CYAN);

                                g.drawLine(p1.x, p1.y, p2.x, p2.y);
                            }
                        }

                        if(vertex.hasObstacle()) {
                            g.setColor(Color.RED);
                        } else {
                            g.setColor(Color.BLACK);
                        }
                        g.fillRect(mapPoint.x, mapPoint.y, 4, 4);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            }

    }
}
