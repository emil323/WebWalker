package scripts.Obstacles;


public class Dialogue {

    private String[] options;

    public Dialogue(String[] options) {
        this.options = options;
    }

    public boolean has(String s) {
        for (String o:this.options) {
            if(o.toLowerCase().equals(s.toLowerCase())) {
                return true;
            }
        }
        return false;
    }


}
