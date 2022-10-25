package energyData.service;

import energyData.domain.EnergyConsumptionEntry;
import energyData.domain.EnergyType;
import energyData.service.db.EnergyConsumptionEntryService;
import energyData.service.db.EnergyTypeService;
import energyData.service.parser.exception.EnergyDataParsingException;
import energyData.service.parser.service.EnergyData;
import energyData.service.parser.service.EnergyDataParser;
import energyData.service.parser.service.EnergyDataValuePair;
import energyData.service.query.exception.EnergyDataQueryException;
import energyData.service.query.service.EnergyDataQueryService;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarBuilder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
     *
     * @param startDate
     * @param endDate
     */
    public void fetchAndSaveData(LocalDate startDate, LocalDate endDate) {
        try {
            List<String> rawDataList = energyDataQueryService.fetchHourlyEnergyData(startDate, endDate);

            List<EnergyData> energyDataList = parseData(rawDataList);

            List<EnergyConsumptionEntry> allEnergyConsumptionEntries = processEnergyDataList(energyDataList);

            System.out.println("\nTotal number of Energy Consumption Entries to save is: " + allEnergyConsumptionEntries.size());
            System.out.println("\nSaving data (saved with saveAll(), so no progress bar available)...\n");
            energyConsumptionEntryService.saveAll(allEnergyConsumptionEntries);
        } catch (EnergyDataQueryException | EnergyDataParsingException e) {
            e.printStackTrace();
        }
    }

    private List<EnergyData> parseData(List<String> requestDataList) {
        List<EnergyData> result = new ArrayList<>();
        ProgressBar progressBar = new ProgressBarBuilder().setInitialMax(requestDataList.size()).setTaskName("Parsing data").build();

        for (String requestData : requestDataList) {
            result.addAll(energyDataParser.parseEnergyDataString(requestData));
            progressBar.step();
        }

        progressBar.close();

        return result;
    }

    private List<EnergyConsumptionEntry> processEnergyDataList(List<EnergyData> energyDataList) {
        List<EnergyConsumptionEntry> allEnergyConsumptionEntries = new ArrayList<>();
        ProgressBar progressBar = new ProgressBarBuilder().setInitialMax(energyDataList.size()).setTaskName("Processing data").build();

        for (EnergyData energyData : energyDataList) {
            addEnergyConsumptionEntries(energyData, allEnergyConsumptionEntries);
            progressBar.step();
        }

        progressBar.close();
        return allEnergyConsumptionEntries;
    }

    private void addEnergyConsumptionEntries(EnergyData energyData, List<EnergyConsumptionEntry> allEnergyConsumptionEntries) {
        EnergyType energyType = getOrCreateEnergyType(energyData.getName());

        for (EnergyDataValuePair energyDataValuePair : energyData.getData()) {
            EnergyConsumptionEntry energyConsumptionEntry = createEnergyConsumptionEntry(energyDataValuePair, energyType);

            if (energyConsumptionEntry != null) {
                allEnergyConsumptionEntries.add(energyConsumptionEntry);
            }
        }
    }

    private EnergyConsumptionEntry createEnergyConsumptionEntry(EnergyDataValuePair energyDataValuePair, EnergyType energyType) {
        if (energyDataValuePair != null && energyDataValuePair.getUnixTimeStamp() == null) return null;

        double energyValueInGw = energyDataValuePair.getEnergyValue() != null ? energyDataValuePair.getEnergyValue() : 0;
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(energyDataValuePair.getUnixTimeStamp()), TimeZone.getDefault().toZoneId());

        return energyConsumptionEntryService.createOrOverrideEnergyConsumptionEntry(energyType, energyValueInGw, dateTime);
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
