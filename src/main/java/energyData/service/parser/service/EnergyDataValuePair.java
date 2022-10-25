package energyData.service.parser.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
@Getter
public class EnergyDataValuePair {
    public EnergyDataValuePair() {
    }

    private Long unixTimeStamp;
    // can be consumption or generation value. Depends on type of energy data
    private Double energyValue;
}
