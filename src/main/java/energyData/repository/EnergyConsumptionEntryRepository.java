package energyData.repository;

import energyData.domain.EnergyConsumptionEntry;
import energyData.domain.EnergyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.Optional;

@Repository
public interface EnergyConsumptionEntryRepository extends JpaRepository<EnergyConsumptionEntry, Long> {
    Optional<EnergyConsumptionEntry> findByEnergyTypeAndDateTimeUtc(EnergyType energyType, Timestamp localDateTime);

    boolean existsByEnergyTypeAndDateTimeUtc(EnergyType energyType, Timestamp localDateTime);
}
