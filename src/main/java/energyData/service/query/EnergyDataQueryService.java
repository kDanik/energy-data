package energyData.service.query;

import netscape.javascript.JSObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriBuilder;

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

    private  final DateFormat apiDateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public EnergyDataQueryService() {
        this.restTemplate = new RestTemplate();
    }

    public List<String> fetchEnergyData(Date startDate, Date endDate) {
        // this method will split request into multiple and combine them to keep data intervals same
        List<String> result = new ArrayList<>(10);

        try {
            result.add(fetchOneEnergyDataChunk(startDate, endDate));
        } catch (URISyntaxException e) {
            System.out.println(e);
            // TODO exception handling
        }

        return result;
    }

    private String fetchOneEnergyDataChunk(Date startDate, Date endDate) throws URISyntaxException {
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

        return new URI("https://www.agora-energiewende.de/service/agorameter/chart/data/power_generation/" + formattedStartDate + "/" + formattedEndDate + "/today/chart.json");
    }
}
