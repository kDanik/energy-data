package energyData.repository;

import energyData.domain.EnergyConsumptionEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnergyConsumptionEntryRepository extends JpaRepository<EnergyConsumptionEntry, Long> {
    Optional<EnergyConsumptionEntry> findByEnergyTypeIdAndTimestamp(Long energyTypeId, Long timestamp);
}
