package scripts.Pathfinding;

import scripts.Graph.Vertex;
import scripts.Requirements.RequirementStack;

import java.util.LinkedHashMap;

/**
 * A path contains a linked hash map of vertieces of a generated path.
 * PathGenerator returns a path
 */

public class Path {


    private LinkedHashMap<String, Vertex> vertices = new LinkedHashMap<>();
    private RequirementStack requirements = new RequirementStack();

    public Vertex last;

    private boolean stuck = false;

    public Path(LinkedHashMap<String,Vertex> vertices, Vertex last, RequirementStack requirementStack) {
        this.vertices = vertices;
        this.last = last;
        this.requirements = requirementStack;
    }

    public Path() {
    }

    /**
     * Add a vertex to path
     * @param vertex
     */

    public void add(Vertex vertex) {
        vertices.put(vertex.getId(), vertex);
        this.last = vertex;
    }

    /**
     * clones this object
     * @return Path
     */
    public Path clone() {
        return new Path((LinkedHashMap<String,Vertex>) this.vertices.clone(), this.last, this.requirements.clone());
    }


    /**
     * Returns true is stuck flah is  set
     * @return
     */
    public boolean stuck() {
        return this.stuck;
    }

    /**
     * Modify stuck status
     * @param stuck
     */

    public void setStuck(boolean stuck) {
        this.stuck = stuck;
    }


    public Vertex end() {
        return this.last;
    }

    public boolean isEmpty() {
        return this.vertices.isEmpty();
    }

    public LinkedHashMap<String,Vertex>  getVertices() {
        return vertices;
    }

    public RequirementStack requirementStack() {
        return requirements;
    }

    public Vertex getByID(String id) {
        return this.vertices.get(id);
    }

    @Override
    public String toString() {
        String desc = "Pathfinding{ vertices=";
        for (String id:this.vertices.keySet()) {
            desc += id + ", ";
        }
        if(this.stuck) desc += "stuck";
        desc +="}";
        return desc;
    }
}
