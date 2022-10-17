package energyData.repository;

import energyData.domain.EnergyConsumptionEntry;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyConsumptionEntryRepository extends CrudRepository<EnergyConsumptionEntry, Long> {
}
