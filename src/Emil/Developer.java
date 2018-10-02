package Emil;

import org.powerbot.script.rt4.ClientContext;
import Emil.Graph.Graph;
import Emil.Pathfinding.Path;

public class Developer {

    private ClientContext ctx;
    private Graph graph;

    private Vertex selected;



    private Path path;

    public Developer(ClientContext ctx, Graph graph) {
        this.ctx = ctx;
        this.graph = graph;
    }

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    public boolean pathAvailable() {
        return path != null;
    }

    public void resetPath() {
        this.path = null;
    }

    public void setSelected(Vertex vertex) {
        this.selected = vertex;
    }

    public Vertex getSelected() {
        return selected;
    }

    public ClientContext getCtx() {
        return ctx;
    }

    public Graph getGraph() {
        return graph;
    }
}
