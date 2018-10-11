package scripts.Requirements;


import org.powerbot.script.rt4.ClientContext;

public interface Requirement {

     /**
      * Interface for different requirements
      * @param ctx
      * @return boolean
      */

     boolean hasRequirement(ClientContext ctx);
}
