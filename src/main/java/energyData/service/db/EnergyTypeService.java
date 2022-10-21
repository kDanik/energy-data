package energyData.service.db;

import energyData.domain.EnergyType;
import energyData.repository.EnergyTypeRepository;
import org.springframework.stereotype.Service;

@Service
public class EnergyTypeService {
    private final EnergyTypeRepository energyTypeRepository;

    public EnergyTypeService(EnergyTypeRepository energyTypeRepository) {
        this.energyTypeRepository = energyTypeRepository;
    }

    public void testStuff() {
        energyTypeRepository.save(new EnergyType());
    }
}
