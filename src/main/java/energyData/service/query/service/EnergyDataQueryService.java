package energyData.service.query.service;

import energyData.service.query.exception.EnergyDataQueryException;
import energyData.service.query.exception.TooBigTimePeriodException;
import energyData.util.DateUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class EnergyDataQueryService {
    private final RestTemplate restTemplate;

    private final DateFormat apiDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public EnergyDataQueryService() {
        this.restTemplate = new RestTemplate();
    }

    /* TODO the return data should be tested to check if it changes time intervals with bigger time period
        (few month period returns data in 1 hour intervals, 1 year data return in... ???)
     */
    public List<String> fetchHourlyEnergyData(Date startDate, Date endDate) {
        // this method will split request into multiple and combine them to keep data intervals same
        List<String> result = new ArrayList<>(10);


        try {
            result.add(fetchOneEnergyDataChunk(startDate, endDate));
        } catch (URISyntaxException | TooBigTimePeriodException | RestClientException e) {
            e.printStackTrace();

            throw new EnergyDataQueryException("Exception while trying to fetch energy data");
        }

        return result;
    }

    private String fetchOneEnergyDataChunk(Date startDate, Date endDate) throws URISyntaxException {
        // if time period is bigger than 28 days (one calendar month) the return data will have
        // time interval of 1 day, instead of required time interval of 1 hour
        if (DateUtil.getDateDifferenceInDays(endDate, startDate) >= 28) throw new TooBigTimePeriodException("The time period for one energy data fetch can't be bigger than 28 days!");

        URI uri = buildServiceURI(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<?> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return result.getBody().toString();
    }

    private URI buildServiceURI(Date startDate, Date endDate) throws URISyntaxException {
        String formattedStartDate = apiDateFormat.format(startDate);
        String formattedEndDate = apiDateFormat.format(endDate);

        // TODO maybe move service url to class attributes or application.properties
        return new URI("https://www.agora-energiewende.de/service/agorameter/chart/data/power_generation/" + formattedStartDate + "/" + formattedEndDate + "/today/chart.json");
    }
}
