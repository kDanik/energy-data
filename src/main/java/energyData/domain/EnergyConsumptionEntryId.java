package energyData.domain;

import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class EnergyConsumptionEntryId implements Serializable {
    private Long timestamp;
    private Long energyType;

    public EnergyConsumptionEntryId(){}

    public EnergyConsumptionEntryId(Long energyType, Long timestamp) {
        this.timestamp = timestamp;
        this.energyType = energyType;
    }
}
