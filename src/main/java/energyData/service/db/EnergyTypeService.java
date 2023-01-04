package energyData.service.db;

import energyData.domain.EnergyType;
import energyData.repository.EnergyTypeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class EnergyTypeService {
    private final EnergyTypeRepository energyTypeRepository;

    public EnergyTypeService(EnergyTypeRepository energyTypeRepository) {
        this.energyTypeRepository = energyTypeRepository;
    }

    public Optional<EnergyType> findEnergyTypeByName(String energyTypeName) {
        return energyTypeRepository.findByName(energyTypeName);
    }

    /**
     * Creates new EnergyType with given name
     * @param energyTypeName unique energy type name. Using duplicate name will throw exception
     * @return new EnergyType entity
     */
    public EnergyType createNewEnergyType(String energyTypeName) {
        EnergyType newEnergyType = new EnergyType();
        newEnergyType.setName(energyTypeName);

        return energyTypeRepository.saveAndFlush(newEnergyType);
    }
}
