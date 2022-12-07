package energyData.service.parser.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;

@Getter
@Setter
@ToString
public class EnergyData {
    private String name;
    // values, Long is timestamp, Double is actual value
    private Map<Long, Double> data;
}
