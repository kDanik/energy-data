package energyData;

import energyData.service.db.EnergyTypeService;
import energyData.service.parser.service.EnergyData;
import energyData.service.parser.service.EnergyDataParser;
import energyData.service.parser.exception.EnergyDataParsingException;
import energyData.service.query.exception.EnergyDataQueryException;
import energyData.service.query.service.EnergyDataQueryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class EnergyDataRunner implements CommandLineRunner {
    private final EnergyTypeService energyTypeService;
    private final EnergyDataQueryService energyDataQueryService;

    private final EnergyDataParser energyDataParser;

    public EnergyDataRunner(EnergyTypeService energyTypeService, EnergyDataQueryService energyDataQueryService, EnergyDataParser energyDataParser) {
        this.energyTypeService = energyTypeService;
        this.energyDataQueryService = energyDataQueryService;
        this.energyDataParser = energyDataParser;
    }

    @Override
    public void run(String... args) {
        LocalDate startDate = LocalDate.of(2022, 2, 1);
        LocalDate endDate = LocalDate.of(2022, 3, 1);

        try {
            List<String> testData = energyDataQueryService.fetchHourlyEnergyData(startDate, endDate);
            List<EnergyData> energyData = energyDataParser.parseEnergyDataString(testData.get(0));
            System.out.println(energyData);
        } catch (EnergyDataQueryException | EnergyDataParsingException e) {
            e.printStackTrace();
        }
    }
}
