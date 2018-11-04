package scripts.Requirements;

import org.powerbot.script.rt4.ClientContext;

import java.util.Arrays;

public class EquipmentRequirement implements Requirement {

    private int[] item_ids;

    public EquipmentRequirement(int[] item_ids) {
        this.item_ids = item_ids;
    }

    @Override
    public boolean isMet(ClientContext ctx) {
        for (int equipped_id: ctx.players.local().appearance()) {
            for(int required_id: this.item_ids){
                if(equipped_id == required_id) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "EquipmentRequirement{" +
                "item_ids=" + Arrays.toString(item_ids) +
                '}';
    }
}
