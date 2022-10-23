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
    public Double energyValue;
}
