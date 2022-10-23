package energyData.service.parser.service;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
public class EnergyData {
    private String name;
    private List<EnergyDataValuePair> data;
}
