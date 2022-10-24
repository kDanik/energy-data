package energyData.repository;

import energyData.domain.EnergyType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EnergyTypeRepository  extends JpaRepository<EnergyType, Long> {
    Optional<EnergyType> findByName(String name);
}
