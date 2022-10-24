package energyData.service.query.service;

import energyData.service.query.exception.EnergyDataQueryException;
import energyData.service.query.exception.TooBigTimePeriodException;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;


import java.net.URI;
import java.net.URISyntaxException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class EnergyDataQueryService {
    private final RestTemplate restTemplate;

    private final DateTimeFormatter apiDateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // if time period is bigger than 28 days (one calendar month) the return data will have
    // time interval of 1 day, instead of required time interval of 1 hour.
    // 28 is obviously minimum possible month size.
    private final int MAXIMUM_TIME_PERIOD_PER_FETCH = 28;

    public EnergyDataQueryService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Fetches energy consumption and generation data for given time period and return it as String array,
     * where each string is data for time period of 28 <= days. So time period of 100 days will return 4 strings in total (28 + 28 + 28 + 16).
     * @param startDate startDate for fetching (result includes startDate)
     * @param endDate end for fetching (result includes endDate)
     * @return String array with request results
     */
    public List<String> fetchHourlyEnergyData(LocalDate startDate, LocalDate endDate) {
        // to keep interval between energy data entries 1 hour this method will split fetching into request-chunks of 28 days each
        List<String> result = new ArrayList<>(10);

        boolean timePeriodFullyIterated = false;
        LocalDate chunkEndDate = startDate;

        while (!timePeriodFullyIterated) {
            chunkEndDate = chunkEndDate.plusDays(MAXIMUM_TIME_PERIOD_PER_FETCH);

            // the new chunkEndDate should never be after endDate. If it reaches endDate, or is after endDate, time period is fully iterated
            if (chunkEndDate.isAfter(endDate)) {
                chunkEndDate = endDate;
                timePeriodFullyIterated = true;
            } else {
                if (chunkEndDate.equals(endDate)) {
                    timePeriodFullyIterated = true;
                }
            }

            try {
                result.add(fetchOneEnergyDataChunk(startDate, chunkEndDate));
            } catch (URISyntaxException | TooBigTimePeriodException | RestClientException e) {
                e.printStackTrace();
                throw new EnergyDataQueryException("Exception while trying to fetch energy data");
            }

            startDate = chunkEndDate.plusDays(1);
        }

        return result;
    }

    /***
     * Fetches one energy data chunk for given time period (maximum 28 days)
     * @param startDate
     * @param endDate
     * @return one energy data chunk
     * @throws URISyntaxException if request URI couldn't be created with given parameters
     */
    private String fetchOneEnergyDataChunk(LocalDate startDate, LocalDate endDate) throws URISyntaxException {
        if (ChronoUnit.DAYS.between(startDate, endDate) > MAXIMUM_TIME_PERIOD_PER_FETCH) {
            throw new TooBigTimePeriodException("The time period for one energy data fetch can't be bigger than 28 days! Input period is: " + ChronoUnit.DAYS.between(startDate, endDate));
        }

        URI uri = buildServiceURI(startDate, endDate);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
        ResponseEntity<?> result = restTemplate.exchange(uri, HttpMethod.GET, entity, String.class);

        return result.getBody().toString();
    }

    private URI buildServiceURI(LocalDate startDate, LocalDate endDate) throws URISyntaxException {
        String formattedStartDate = apiDateFormat.format(startDate);
        String formattedEndDate = apiDateFormat.format(endDate);

        // TODO maybe move service url to class attributes or application.properties
        return new URI("https://www.agora-energiewende.de/service/agorameter/chart/data/power_generation/" + formattedStartDate + "/" + formattedEndDate + "/today/chart.json");
    }
}
