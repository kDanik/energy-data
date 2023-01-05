package energyData.service;

import energyData.domain.EnergyConsumptionEntry;
import energyData.domain.EnergyType;
import energyData.service.db.EnergyConsumptionEntryService;
import energyData.service.db.EnergyTypeService;
import energyData.service.parser.service.EnergyData;
import energyData.service.parser.service.EnergyDataParser;
import energyData.service.parser.service.EnergyDataValuePair;
import energyData.service.query.service.EnergyDataQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class EnergyDataService {
    private final EnergyTypeService energyTypeService;
    private final EnergyConsumptionEntryService energyConsumptionEntryService;
    private final EnergyDataQueryService energyDataQueryService;
    private final EnergyDataParser energyDataParser;

    private final Logger LOG = LoggerFactory.getLogger(EnergyDataService.class);

    public EnergyDataService(EnergyTypeService energyTypeService, EnergyConsumptionEntryService energyConsumptionEntryService, EnergyDataQueryService energyDataQueryService, EnergyDataParser energyDataParser) {
        this.energyTypeService = energyTypeService;
        this.energyConsumptionEntryService = energyConsumptionEntryService;
        this.energyDataQueryService = energyDataQueryService;
        this.energyDataParser = energyDataParser;
    }

    /**
     * Fetches, parses and saves / overrides energy data for given time period
     *
     * @param startDate
     * @param endDate
     */
    public void fetchAndSaveData(LocalDate startDate, LocalDate endDate) {
        LOG.error("Start data fetching");
        List<String> rawDataList = energyDataQueryService.fetchHourlyEnergyData(startDate, endDate);

        LOG.error("End data fetching. Start data parsing.");
        List<EnergyData> energyDataList = energyDataParser.parseEnergyData(rawDataList);

        LOG.error("End data parsing. Start data processing.");
        List<EnergyConsumptionEntry> allEnergyConsumptionEntries = processEnergyDataList(energyDataList);

        LOG.error("End data processing. Start data saving.");
        energyConsumptionEntryService.saveAll(allEnergyConsumptionEntries);

        LOG.error("End data saving.");
    }

    private List<EnergyConsumptionEntry> processEnergyDataList(List<EnergyData> energyDataList) {
        LinkedList<EnergyConsumptionEntry> allEnergyConsumptionEntries = new LinkedList<>();

        for (EnergyData energyData : energyDataList) {
            addEnergyConsumptionEntries(energyData, allEnergyConsumptionEntries);
        }

        return allEnergyConsumptionEntries;
    }

    private void addEnergyConsumptionEntries(EnergyData energyData, LinkedList<EnergyConsumptionEntry> allEnergyConsumptionEntries) {
        EnergyType energyType = getOrCreateEnergyType(energyData.getName());

        for (EnergyDataValuePair energyValuePair : energyData.getTimestampValuePairs()) {
            EnergyConsumptionEntry energyConsumptionEntry = createEnergyConsumptionEntry(energyValuePair, energyType);

            if (energyConsumptionEntry != null) {
                allEnergyConsumptionEntries.addLast(energyConsumptionEntry);
            }
        }
    }

    private EnergyConsumptionEntry createEnergyConsumptionEntry(EnergyDataValuePair energyDataValuePair, EnergyType energyType) {
        if (energyDataValuePair.getUnixTimeStamp() != null && energyDataValuePair.getEnergyValue() == null) return null;

        double energyValueInGw = energyDataValuePair.getEnergyValue() != null ? energyDataValuePair.getEnergyValue() : 0;

        return energyConsumptionEntryService.createNewEntry(energyType, energyValueInGw, energyDataValuePair.getUnixTimeStamp());
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
