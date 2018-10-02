package scripts;



import org.powerbot.script.*;
import org.powerbot.script.rt4.ClientContext;
import scripts.Graph.Graph;
import scripts.Graph.GraphUtils;
import scripts.Pathfinding.Traverser;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;


/**
 * This script is made to test walkweb and a contains a GUI to create new vertices.
 */

@Script.Manifest(name="Walkweb", description="cuts willow logs and drops them", properties = "author=Emil;client=4")
public class Walkweb extends PollingScript<ClientContext> implements PaintListener{

    private Graph graph;
    private DeveloperGUI gui;
    private Developer dev;

    @Override
    public void start() {
        super.start();
        try {
            graph = GraphUtils.loadXML("C:\\Users\\Emil\\Rsbot_walkweb\\graph.xml");
            GraphUtils.loadObstaclesXML(graph, "C:\\Users\\Emil\\Rsbot_walkweb\\surface.obstacles.xml");

            GraphUtils.loadObstaclesXML(graph, "C:\\Users\\Emil\\Rsbot_walkweb\\security_stronghold.obstacles.xml");
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
                    if (ctx.players.local().tile().distanceTo(tile) < 25) {
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
                        g.setColor(Color.YELLOW);
                        g.clearRect(mapPoint.x, mapPoint.y, 5, 5);

                        //Draw lines between edges on minimap

                        ArrayList<Vertex> edges = vertex.getEdges();

                        for (Vertex edge : edges) {
                            //This will draw double, but does not matter.
                            Point p1 = edge.tile().matrix(ctx).mapPoint();
                            Point p2 = vertex.tile().matrix(ctx).mapPoint();
                            g.setColor(Color.CYAN);
                            g.drawLine(p1.x, p1.y, p2.x, p2.y);
                        }

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            }

    }
}
