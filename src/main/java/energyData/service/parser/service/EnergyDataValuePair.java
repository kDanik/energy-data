package energyData.service.parser.service;

import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
public class EnergyDataValuePair {
    public EnergyDataValuePair(Long unixTimeStamp, Double energyValue) {
        this.unixTimeStamp = unixTimeStamp;
        this.energyValue = energyValue;
    }

    public Long unixTimeStamp;
    // can be consumption or generation value. Depends on type of energy data
    public Double energyValue;
}
