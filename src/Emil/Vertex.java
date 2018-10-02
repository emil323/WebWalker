package Emil;

import org.powerbot.script.rt4.ClientContext;
import org.powerbot.script.Tile;
import Emil.Obstacles.Obstacle;

import java.util.ArrayList;
import org.powerbot.script.Random;


public class Vertex {

    private String id;
    private Tile position;
    private int leeway; //The leeway this vertex has

    private Obstacle obstacle;
    private boolean is_bank;

    private ArrayList<Vertex> edges = new ArrayList<Vertex>();

    public Vertex(String id, Tile position, int leeway, boolean is_bank) {
        this.id = id;
        this.position = position;
        this.leeway = leeway;
        this.is_bank = is_bank;
    }

    public ArrayList<Vertex> getEdges() {
        return edges;
    }

    public Tile tile() {
        return this.position;
    }
    public Tile getRandomisedTile() {
        return this.position.derive( Random.nextInt(-leeway+1, leeway-1),Random.nextInt(-leeway+1, leeway-1)); //leeway -1 to compensate for inaccuaracies
    }

    public String getId() {
        return id;
    }

    public void addEdge(Vertex vertex) {
        this.edges.add(vertex);
    }

    public void setObstacle(Obstacle obstacle) {
        this.obstacle = obstacle;
    }

    public Obstacle getObstacle() {
        return obstacle;
    }

    public boolean hasObstacle() {
        return obstacle != null;
    }

    public int getLeeway() {
        return leeway;
    }

    public boolean isBank() {
        return is_bank;
    }

    public boolean meetsObstacleRequirements(ClientContext ctx, Vertex candidate) {
        if(this.obstacle != null) {
            if(obstacle.needToComplete(candidate)) {
                if(!obstacle.hasRequirements(ctx)){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vertex vertex = (Vertex) o;

        return id != null ? id.equals(vertex.id) : vertex.id == null;
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + position.hashCode();
        result = 31 * result + (edges != null ? edges.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "id='" + id + '\'' +
                ", position=" + position +
                ", obstacle=" + obstacle +
                ", edges=" + edges.size() +
                '}';
    }
}
