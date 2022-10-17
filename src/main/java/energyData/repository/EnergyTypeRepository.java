package energyData.repository;

import energyData.domain.EnergyType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnergyTypeRepository  extends CrudRepository<EnergyType, Long> {
}
