package energyData.service.db;

import energyData.domain.EnergyConsumptionEntry;
import energyData.domain.EnergyType;
import energyData.repository.EnergyConsumptionEntryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EnergyConsumptionEntryService {
    private final EnergyConsumptionEntryRepository energyConsumptionEntryRepository;

    public EnergyConsumptionEntryService(EnergyConsumptionEntryRepository energyConsumptionEntryRepository) {
        this.energyConsumptionEntryRepository = energyConsumptionEntryRepository;
    }

    public void saveAll(List<EnergyConsumptionEntry> allEnergyConsumptionEntries) {
        energyConsumptionEntryRepository.saveAllAndFlush(allEnergyConsumptionEntries);
    }

    /**
     * Creates new entry or overrides existing entry with given data. DOES NOT save entry into database.
     * Due to performance reasons saving should be done to entire list, instead of each entry separately.
     * @param energyType
     * @param energyValueInGw energy value in gigawatt
     * @param dateTime
     * @return new EnergyConsumptionEntry entry or overridden EnergyConsumptionEntry entry
     */
    public EnergyConsumptionEntry createOrOverrideEnergyConsumptionEntry(EnergyType energyType, Double energyValueInGw, LocalDateTime dateTime) {
        Optional<EnergyConsumptionEntry> consumptionEntryOptional = energyConsumptionEntryRepository.findByEnergyTypeAndDateTime(energyType, dateTime);
        if (consumptionEntryOptional.isPresent()) {
            return overrideValueOfExistingEntry(consumptionEntryOptional.get(), energyValueInGw);
        } else {
            return createNewEntry(energyType, energyValueInGw, dateTime);
        }
    }

    private EnergyConsumptionEntry overrideValueOfExistingEntry(EnergyConsumptionEntry energyConsumptionEntry, Double energyValueInGw) {
        energyConsumptionEntry.setValueInGw(energyValueInGw);

        return energyConsumptionEntry;
    }

    private EnergyConsumptionEntry createNewEntry(EnergyType energyType, Double energyValueInGw, LocalDateTime dateTime) {
        EnergyConsumptionEntry energyConsumptionEntry = new EnergyConsumptionEntry();

        energyConsumptionEntry.setEnergyType(energyType);
        energyConsumptionEntry.setValueInGw(energyValueInGw);
        energyConsumptionEntry.setDateTime(dateTime);

        return energyConsumptionEntry;
    }
}
