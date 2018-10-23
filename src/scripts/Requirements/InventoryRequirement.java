package scripts.Requirements;

import org.powerbot.script.rt4.ClientContext;

public class InventoryRequirement implements Requirement {

    private int itemID;
    private boolean stackable = false;
    /**
     * When removed is true, it means that the amount of the item is removed everytime the obstacle is passed (toll-gate, etc...)
     */
    private boolean removed = false;
    private int amount = -1;

    public InventoryRequirement(int itemID) {
        this.itemID = itemID;
    }

    public InventoryRequirement(int itemID, int amount) {
        this.itemID = itemID;
        this.amount = amount;
    }

    public InventoryRequirement(int itemID, int amount, boolean stackable, boolean removed) {
        this.itemID = itemID;
        this.amount = amount;
        this.stackable = stackable;
        this.removed = removed;
    }

    public int getItemID() {
        return itemID;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public boolean isMet(ClientContext ctx) {
        if(this.amount > 0) {
            return ctx.inventory.select().id(this.itemID).count(true) >= this.amount;
        } else {
            return !ctx.inventory.select().id(this.itemID).isEmpty();
        }
    }

    /**
     * Append value (by addition) to amount and return as new InventoryRequirement
     * @param other
     */

    @Override
    public Requirement sumClone(Requirement other) {
        int newAmount = this.amount + ((InventoryRequirement) other).getAmount();
        return new InventoryRequirement(this.itemID, newAmount, this.stackable,this.removed);
    }

    /**
     * Method to check if summable with other requirement
     * @param other
     * @return
     */

    @Override
    public boolean summable(Requirement other) {

        if(!this.removed) return false;
        //Check if same instance type
        if(other instanceof InventoryRequirement) {
            InventoryRequirement otherInvReq = (InventoryRequirement) other;
            //Custom code
            if(otherInvReq.getItemID() == this.itemID) {
                return true;
            }
        }
        return false;
    }


    @Override
    public String toString() {
        return "InventoryRequirement{" +
                "itemID=" + itemID +
                ", stackable=" + stackable +
                ", amount=" + amount +
                '}';
    }
}
