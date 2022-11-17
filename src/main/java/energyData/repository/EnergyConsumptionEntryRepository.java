package energyData.repository;

import energyData.domain.EnergyConsumptionEntry;
import energyData.domain.EnergyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EnergyConsumptionEntryRepository extends JpaRepository<EnergyConsumptionEntry, Long> {
    Optional<EnergyConsumptionEntry> findByEnergyTypeAndDateTime(EnergyType energyType, LocalDateTime localDateTime);

    boolean existsByEnergyTypeAndDateTime(EnergyType energyType, LocalDateTime localDateTime);
}
