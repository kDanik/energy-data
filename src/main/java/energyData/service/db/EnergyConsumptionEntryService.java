package energyData.service.db;

import energyData.domain.EnergyConsumptionEntry;
import energyData.domain.EnergyType;
import energyData.repository.EnergyConsumptionEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EnergyConsumptionEntryService {
    private final EnergyConsumptionEntryRepository energyConsumptionEntryRepository;

    private final Logger LOG = LoggerFactory.getLogger(EnergyConsumptionEntryService.class);

    public EnergyConsumptionEntryService(EnergyConsumptionEntryRepository energyConsumptionEntryRepository) {
        this.energyConsumptionEntryRepository = energyConsumptionEntryRepository;
    }

    /**
     * Batch saves list of EnergyConsumptionEntry-s in one operation
     */
    public void saveAll(List<EnergyConsumptionEntry> allEnergyConsumptionEntries) {
        energyConsumptionEntryRepository.saveAll(allEnergyConsumptionEntries);
    }

    /**
     * Creates new entry. DOES NOT save entry into database.
     * Due to performance reasons saving should be done to entire list, instead of each entry separately.
     *
     * @param energyValueInGw energy value in gigawatt
     * @return new EnergyConsumptionEntry entry or overridden EnergyConsumptionEntry entry
     */
    public EnergyConsumptionEntry createNewEntry(EnergyType energyType, Double energyValueInGw, Long timestamp) {
        EnergyConsumptionEntry energyConsumptionEntry = new EnergyConsumptionEntry();

        energyConsumptionEntry.setEnergyType(energyType);
        energyConsumptionEntry.setValueInMw(energyValueInGw);
        energyConsumptionEntry.setTimestamp(timestamp);

        return energyConsumptionEntry;
    }
}
