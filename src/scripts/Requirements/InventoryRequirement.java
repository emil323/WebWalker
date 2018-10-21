package scripts.Requirements;

import org.powerbot.script.rt4.ClientContext;

public class InventoryRequirement implements Requirement {

    private int itemID;
    private boolean stackable = false;
    private int amount = -1;

    public InventoryRequirement(int itemID) {
        this.itemID = itemID;
    }

    public InventoryRequirement(int itemID, int amount) {
        this.itemID = itemID;
        this.amount = amount;
    }

    public InventoryRequirement(int itemID, int amount, boolean stackable) {
        this.itemID = itemID;
        this.amount = amount;
        this.stackable = stackable;
    }

    @Override
    public boolean isMet(ClientContext ctx) {
        if(this.amount > 0) {
            return ctx.inventory.select().id(this.itemID).count(true) > this.amount;
        } else {
            return !ctx.inventory.select().id(this.itemID).isEmpty();
        }
    }
}
