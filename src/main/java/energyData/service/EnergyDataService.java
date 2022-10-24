package energyData.service;

import energyData.domain.EnergyType;
import energyData.service.db.EnergyConsumptionEntryService;
import energyData.service.db.EnergyTypeService;
import energyData.service.parser.exception.EnergyDataParsingException;
import energyData.service.parser.service.EnergyData;
import energyData.service.parser.service.EnergyDataParser;
import energyData.service.parser.service.EnergyDataValuePair;
import energyData.service.query.exception.EnergyDataQueryException;
import energyData.service.query.service.EnergyDataQueryService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

@Service
public class EnergyDataService {
    private final EnergyTypeService energyTypeService;
    private final EnergyConsumptionEntryService energyConsumptionEntryService;
    private final EnergyDataQueryService energyDataQueryService;
    private final EnergyDataParser energyDataParser;

    public EnergyDataService(EnergyTypeService energyTypeService, EnergyConsumptionEntryService energyConsumptionEntryService, EnergyDataQueryService energyDataQueryService, EnergyDataParser energyDataParser) {
        this.energyTypeService = energyTypeService;
        this.energyConsumptionEntryService = energyConsumptionEntryService;
        this.energyDataQueryService = energyDataQueryService;
        this.energyDataParser = energyDataParser;
    }

    /**
     * Fetches, parses and saves / overrides energy data for given time period
     * @param startDate
     * @param endDate
     */
    public void fetchAndSaveData(LocalDate startDate, LocalDate endDate) {
        try {
            System.out.println("\nFetching data...\n");
            List<String> rawDataList = energyDataQueryService.fetchHourlyEnergyData(startDate, endDate);

            System.out.println("\nParsing data...\n");
            List<EnergyData> energyDataList = energyDataParser.parseEnergyDataList(rawDataList);

            System.out.println("\nSaving data...\n");
            saveEnergyDataList(energyDataList);

        } catch (EnergyDataQueryException | EnergyDataParsingException e) {
            e.printStackTrace();
        }
    }

    private void saveEnergyDataList(List<EnergyData> energyDataList) {
        for (EnergyData energyData : energyDataList) {
            saveEnergyData(energyData);
        }
    }

    private void saveEnergyData(EnergyData energyData) {
        EnergyType energyType = getOrCreateEnergyType(energyData.getName());

        for (EnergyDataValuePair energyDataValuePair : energyData.getData()) {
            saveOneEnergyDataPair(energyDataValuePair, energyType);
        }
    }

    private void saveOneEnergyDataPair(EnergyDataValuePair energyDataValuePair, EnergyType energyType) {
        if (energyDataValuePair != null && energyDataValuePair.getUnixTimeStamp() == null) return;

        double energyValueInGw = energyDataValuePair.getEnergyValue() != null ? energyDataValuePair.getEnergyValue() : 0;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(energyDataValuePair.getUnixTimeStamp()), TimeZone.getDefault().toZoneId());

        energyConsumptionEntryService.saveEnergyConsumptionEntry(energyType, energyValueInGw, dateTime);
    }

    private EnergyType getOrCreateEnergyType(String energyTypeName) {
        Optional<EnergyType> energyTypeOptional = energyTypeService.findEnergyTypeByName(energyTypeName);

        if (energyTypeOptional.isEmpty()) {
            return energyTypeService.createNewEnergyType(energyTypeName);
        } else {
            return energyTypeOptional.get();
        }
    }
}
