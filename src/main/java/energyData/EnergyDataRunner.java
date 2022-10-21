package energyData;

import energyData.service.db.EnergyTypeService;
import energyData.service.parser.EnergyData;
import energyData.service.parser.EnergyDataParser;
import energyData.service.query.EnergyDataQueryService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class EnergyDataRunner implements CommandLineRunner {
    private final EnergyTypeService energyTypeService;
    private final EnergyDataQueryService energyDataQueryService;

    private final EnergyDataParser energyDataParser;

    public EnergyDataRunner(EnergyTypeService energyTypeService, EnergyDataQueryService energyDataQueryService, EnergyDataParser energyDataParser) {
        this.energyTypeService = energyTypeService;
        this.energyDataQueryService = energyDataQueryService;
        this.energyDataParser = energyDataParser;
    }

    @Override
    public void run(String... args) throws Exception {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date start = cal.getTime();

        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, Calendar.JULY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        List<String> testData = energyDataQueryService.fetchEnergyData(start, end);

        List<EnergyData> energyData = energyDataParser.parseEnergyDataString(testData.get(0));
        System.out.println(energyData);
    }
}
