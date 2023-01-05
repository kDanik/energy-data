package energyData.repository;

import energyData.domain.EnergyConsumptionEntry;
import energyData.domain.EnergyConsumptionEntryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnergyConsumptionEntryRepository extends JpaRepository<EnergyConsumptionEntry, EnergyConsumptionEntryId> {
    Optional<EnergyConsumptionEntry> findByEnergyTypeIdAndTimestamp(Long energyTypeId, Long timestamp);
}
