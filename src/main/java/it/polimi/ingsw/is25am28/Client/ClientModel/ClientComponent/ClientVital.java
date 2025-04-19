package it.polimi.ingsw.is25am28.Client.ClientModel.ClientComponent;

import it.polimi.ingsw.is25am28.Model.Components.VitalType;
import it.polimi.ingsw.is25am28.Model.Connector;

import java.util.List;

public final class ClientVital extends ClientComponent {
    private final VitalType vitalType;

    public ClientVital(int id, List<Integer> sides, int type) {
        super(id, sides);

        if (type == VitalType.BROWN_VITAL.ordinal()) {
            this.vitalType = VitalType.BROWN_VITAL;
        }
        else if (type == VitalType.PURPLE_VITAL.ordinal()) {
            this.vitalType = VitalType.PURPLE_VITAL;
        }
        else {
            throw new IllegalArgumentException("ERROR: Given vital type is not recognized");
        }
    }

    public VitalType getVitalType() {
        return this.vitalType;
    }
}
