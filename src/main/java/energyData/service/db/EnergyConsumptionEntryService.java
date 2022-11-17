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
        // due to some errors in API of agora (there is duplicated entry for one hour on 30.10.2022) this has to be done like that, instead of using saveAllAndFlush()

        for (EnergyConsumptionEntry energyConsumptionEntry : allEnergyConsumptionEntries) {
            // before saving, we should check if entry is not duplicate
            // if id is new (or null) and entry exists with same type and dateTime, it is duplicate
            if (energyConsumptionEntry.getId() == null || !energyConsumptionEntryRepository.existsById(energyConsumptionEntry.getId())) {
                if (energyConsumptionEntryRepository.existsByEnergyTypeAndDateTime(energyConsumptionEntry.getEnergyType(), energyConsumptionEntry.getDateTime())) {
                    System.out.println("Duplicate entry detected! check your implementation or agora api for errors: " + energyConsumptionEntry);

                    continue;
                }
            }

            energyConsumptionEntryRepository.saveAndFlush(energyConsumptionEntry);
        }
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
        energyConsumptionEntry.setValueInMw(energyValueInGw);

        return energyConsumptionEntry;
    }

    private EnergyConsumptionEntry createNewEntry(EnergyType energyType, Double energyValueInGw, LocalDateTime dateTime) {
        EnergyConsumptionEntry energyConsumptionEntry = new EnergyConsumptionEntry();

        energyConsumptionEntry.setEnergyType(energyType);
        energyConsumptionEntry.setValueInMw(energyValueInGw);
        energyConsumptionEntry.setDateTime(dateTime);

        return energyConsumptionEntry;
    }
}
