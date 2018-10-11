package scripts.Dialogue;


import scripts.Dialogue.Dialogue;

import java.util.HashMap;

public class Dialogues {


    private HashMap<String,Dialogue> dialogues = new HashMap<>();

    public Dialogues(){}


    public void addDialogue(Dialogue d) {
        this.dialogues.put(d.getID(), d);
    }

    public Dialogue findByID(String id) {
        return dialogues.get(id);
    }



    @Override
    public String toString() {
        String desc =  "Dialogue{";
        for(Dialogue d:this.dialogues.values())
            desc+= d.toString();
        desc +=  '}';
        return desc;
    }
}
