package Emil.Graph;

import org.powerbot.script.Tile;
import org.powerbot.script.rt4.ClientContext;
import Emil.Vertex;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Graph {

    private Map<String, Vertex> vertices = new HashMap<String, Vertex>();

    public Graph() {
    }


    public Vertex findNode(String id) {
        return this.vertices.get(id);
    }

    public void addNode(Vertex vertex) {
        this.vertices.put(vertex.getId(), vertex);
    }

    public void addEdge(Vertex vertex, Vertex edge) {
        vertex.addEdge(edge);
    }
    public Iterator<Vertex> iterator() {
        return vertices.values().iterator();
    }

    public String nextID() {
        return Integer.toString(this.vertices.size());
    }

    /**
     * Get nearest vertex
     * @return
     */

    public Vertex getNearestVertex(Tile tile) {

        double closest = Double.MAX_VALUE;
        Vertex current = null;
        Iterator<Vertex> iterator = this.iterator();
        while (iterator.hasNext()) {
            Vertex vertex =iterator.next();
            if(vertex.tile().distanceTo(tile) < closest) {
                current = vertex;
                closest = vertex.tile().distanceTo(tile);
            }
        }
        return current;
    }

    public Vertex getNearestReachableVertex(ClientContext ctx) {

        double closest = Double.MAX_VALUE;
        Vertex current = null;
        Tile playerTile = ctx.players.local().tile();
        Iterator<Vertex> iterator = this.iterator();

        while (iterator.hasNext()) {
            Vertex vertex =iterator.next();
            if(playerTile.distanceTo(vertex.tile()) < 30) {
                if (ctx.movement.reachable(playerTile, vertex.tile())) {
                    if (vertex.tile().distanceTo(playerTile) < closest) {
                        current = vertex;
                        closest = vertex.tile().distanceTo(playerTile);
                    }
                }
            }
        }
        return current;
    }


    @Override
    public String toString() {
        String desc = "Graph {";
        for (Vertex vertex:this.vertices.values()) {
            if(vertex.getObstacle() != null)
            desc += vertex.toString();
        }
        return desc;
    }
}
