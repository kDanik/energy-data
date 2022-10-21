package energyData.service.parser;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Setter
public class EnergyDataValuePair {
    public EnergyDataValuePair(Long firstValue, Double secondValue) {
        this.firstValue = firstValue;
        this.secondValue = secondValue;
    }
    public Long firstValue;
    public Double secondValue;
}
