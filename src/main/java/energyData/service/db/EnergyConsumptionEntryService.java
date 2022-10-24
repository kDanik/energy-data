package energyData.service.db;

import energyData.domain.EnergyConsumptionEntry;
import energyData.domain.EnergyType;
import energyData.repository.EnergyConsumptionEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EnergyConsumptionEntryService {
    private final EnergyConsumptionEntryRepository energyConsumptionEntryRepository;

    public EnergyConsumptionEntryService(EnergyConsumptionEntryRepository energyConsumptionEntryRepository) {
        this.energyConsumptionEntryRepository = energyConsumptionEntryRepository;
    }

    /**
     * Saves new entry or overrides existing entry with given data
     * @param energyType
     * @param energyValueInGw energy value in gigawatt
     * @param dateTime
     * @return new EnergyConsumptionEntry entry or overridden EnergyConsumptionEntry entry
     */
    public EnergyConsumptionEntry saveEnergyConsumptionEntry(EnergyType energyType, Double energyValueInGw, LocalDateTime dateTime) {
        Optional<EnergyConsumptionEntry> consumptionEntryOptional = energyConsumptionEntryRepository.findByEnergyTypeAndDateTime(energyType, dateTime);

        if (consumptionEntryOptional.isPresent()) {
            return overrideValueOfExistingEntry(consumptionEntryOptional.get(), energyValueInGw);
        } else {
            return saveNewEntry(energyType, energyValueInGw, dateTime);
        }
    }

    private EnergyConsumptionEntry overrideValueOfExistingEntry(EnergyConsumptionEntry energyConsumptionEntry, Double energyValueInGw) {
        energyConsumptionEntry.setValueInGw(energyValueInGw);

        return energyConsumptionEntryRepository.saveAndFlush(energyConsumptionEntry);
    }

    private EnergyConsumptionEntry saveNewEntry(EnergyType energyType, Double energyValueInGw, LocalDateTime dateTime) {
        EnergyConsumptionEntry energyConsumptionEntry = new EnergyConsumptionEntry();

        energyConsumptionEntry.setEnergyType(energyType);
        energyConsumptionEntry.setValueInGw(energyValueInGw);
        energyConsumptionEntry.setDateTime(dateTime);

        return energyConsumptionEntryRepository.saveAndFlush(energyConsumptionEntry);
    }
}
