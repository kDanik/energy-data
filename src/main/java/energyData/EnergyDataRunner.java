package energyData;

import energyData.service.db.EnergyTypeService;
import energyData.service.parser.service.EnergyData;
import energyData.service.parser.service.EnergyDataParser;
import energyData.service.parser.exception.EnergyDataParsingException;
import energyData.service.query.exception.EnergyDataQueryException;
import energyData.service.query.service.EnergyDataQueryService;
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
    public void run(String... args) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, Calendar.AUGUST);
        cal.set(Calendar.DAY_OF_MONTH, 10);
        Date start = cal.getTime();

        cal.set(Calendar.YEAR, 2022);
        cal.set(Calendar.MONTH, Calendar.SEPTEMBER);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date end = cal.getTime();

        try{
            List<String> testData = energyDataQueryService.fetchHourlyEnergyData(start, end);
            List<EnergyData> energyData = energyDataParser.parseEnergyDataString(testData.get(0));
            System.out.println(energyData);
        } catch (EnergyDataQueryException | EnergyDataParsingException e) {
            e.printStackTrace();
        }
    }
}
