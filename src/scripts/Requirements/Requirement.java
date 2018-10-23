package scripts.Requirements;


import org.powerbot.script.rt4.ClientContext;

public interface Requirement {

     /**
      * Interface for different requirements
      * @param ctx
      * @return boolean
      */

     boolean isMet(ClientContext ctx);

     /**
      *
      * True if requirement is summable with another requirement, not that different from equals
      * An example of a summable requirement:
      * -30GP required for ferry ride
      * An example of a unsummable requirement:
      * -level 30 agility for shortcut (no reason to sum levels)
      *
      * @return true if summable
      */

     default boolean summable(Requirement other) {
          return false;
     }

     /**
      * Method for appending value, if summable
      * @param other
      */


     default Requirement sumClone(Requirement other) {
          throw new UnsupportedOperationException();
     }

}
