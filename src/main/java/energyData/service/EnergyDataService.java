package energyData.service;

import energyData.service.db.EnergyTypeService;
import energyData.service.parser.exception.EnergyDataParsingException;
import energyData.service.parser.service.EnergyData;
import energyData.service.parser.service.EnergyDataParser;
import energyData.service.query.exception.EnergyDataQueryException;
import energyData.service.query.service.EnergyDataQueryService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnergyDataService {
    private final EnergyTypeService energyTypeService;
    private final EnergyDataQueryService energyDataQueryService;
    private final EnergyDataParser energyDataParser;

    public EnergyDataService(EnergyTypeService energyTypeService, EnergyDataQueryService energyDataQueryService, EnergyDataParser energyDataParser) {
        this.energyTypeService = energyTypeService;
        this.energyDataQueryService = energyDataQueryService;
        this.energyDataParser = energyDataParser;
    }

    public void fetchAndSaveData(LocalDate startDate, LocalDate endDate) {
        try {
            List<String> testData = energyDataQueryService.fetchHourlyEnergyData(startDate, endDate);

            List<EnergyData> energyData = energyDataParser.parseEnergyDataList(testData);

            System.out.println(energyData);

        } catch (EnergyDataQueryException | EnergyDataParsingException e) {
            e.printStackTrace();
        }
    }
}
