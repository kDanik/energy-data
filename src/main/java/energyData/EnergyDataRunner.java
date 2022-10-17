package energyData;

import energyData.service.EnergyTypeService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class EnergyDataRunner implements CommandLineRunner {
    private final EnergyTypeService energyTypeService;

    public EnergyDataRunner(EnergyTypeService energyTypeService) {
        this.energyTypeService = energyTypeService;
    }

    @Override
    public void run(String... args) throws Exception {
        energyTypeService.testStuff();
    }
}
