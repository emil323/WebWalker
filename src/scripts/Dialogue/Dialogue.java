package scripts.Dialogue;


import java.util.Arrays;

public class Dialogue {

    private String id;
    private String[] options;

    public Dialogue(String id, String[] options) {
        this.id = id;
        this.options = options;
    }


    public String getID() {
        return id;
    }

    public String[] options() {
        return options;
    }

    @Override
    public String toString() {
        return "Dialogue{" +
                "id='" + id + '\'' +
                ", options=" + Arrays.toString(options) +
                '}';
    }
}
