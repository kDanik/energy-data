package energyData.service.parser.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class EnergyDataValuePair {
    public EnergyDataValuePair(Long unixTimeStamp, Double energyValue) {
        this.unixTimeStamp = unixTimeStamp;
        this.energyValue = energyValue;
    }

    private Long unixTimeStamp;
    // can be consumption or generation value. Depends on type of energy data
    private Double energyValue;
}
