package energyData.service.parser.service;

import energyData.service.parser.exception.EnergyDataParsingException;
import energyData.util.HTMLEscape;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
public class EnergyDataParser {
    private final Logger LOG = LoggerFactory.getLogger(EnergyDataParser.class);

    public List<EnergyData> parseEnergyData(List<String> requestDataList) {
        List<EnergyData> result = new ArrayList<>();

        for (String requestData : requestDataList) {
            result.addAll(parseEnergyDataString(requestData));
        }

        return result;
    }

    /**
     * Parses request body with energy data
     *
     * @param requestData
     * @return List of EnergyData (names and values)
     */
    private List<EnergyData> parseEnergyDataString(String requestData) {
        List<EnergyData> result = new ArrayList<>();

        String[] splitByTypeData = splitByType(getJsContent(requestData));

        for (int i = 1; i < splitByTypeData.length; i++) {
            result.add(extractData(splitByTypeData[i]));
        }

        return result;
    }

    /**
     * Searches for part of response string with js content
     *
     * @param requestData String with request body
     * @return new String with only js part of request body
     */
    private String getJsContent(String requestData) {
        String startSearchString = "js\":\""; // js part start with js tag
        String endSearchString = "\"html\":"; // js part ends with html tag

        try {
            int start = requestData.indexOf(startSearchString) + startSearchString.length();
            int end = requestData.lastIndexOf(endSearchString) - endSearchString.length() + 1;

            return requestData.substring(start, end);
        } catch (IndexOutOfBoundsException e) {
            LOG.error("IndexOutOfBoundsException while parsing request data: " + requestData, e);

            throw new EnergyDataParsingException();
        }
    }

    /**
     * Splits string with js content of request body by their type and return string array (without first element!)
     *
     * @param jsContent String with js content of request body
     * @return String array
     */
    private String[] splitByType(String jsContent) {
        String[] temp = jsContent.split("name\\\\\":");
        temp = Arrays.copyOfRange(temp, 1, temp.length);

        if (temp.length == 0)
            throw new EnergyDataParsingException("Exception while parsing energy data, splitByType() resulted into empty array!");

        return temp;
    }

    /**
     * Parses one string from array {@link this.splitByType()}, getting its name and values
     *
     * @param splitByTypeData one splitByType string from splitByType array {@link this.splitByType()}
     * @return EnergyData with parse result
     */
    private EnergyData extractData(String splitByTypeData) {
        EnergyData energyData = new EnergyData();

        energyData.setName(HTMLEscape.removeAllHtmlTags(extractName(splitByTypeData)));
        energyData.setTimestampValuePairs(extractValuesPairs(splitByTypeData));

        return energyData;
    }

    /**
     * finds and return name of energy type
     *
     * @param data splitByTypeData String
     * @return name of energy type (unformatted and possibly with html tags)
     */
    private String extractName(String data) {
        String startString = "\"";

        int start = data.indexOf(startString);
        int end = data.indexOf("\\\"", start + startString.length());

        return data.substring(start + startString.length(), end);
    }

    /**
     * extracts all value pairs from splitByTypeData string
     *
     * @param data see splitByTypeData
     * @return linked list extracted values
     */
    private LinkedList<EnergyDataValuePair> extractValuesPairs(String data) {
        LinkedList<EnergyDataValuePair> valuesPairs = new LinkedList<>();

        int currentIndex = findStartOfValuesArray(data);

        while (currentIndex != -1 && data.charAt(currentIndex) == '[') {
            currentIndex = addNextValuePair(data, currentIndex, valuesPairs);
        }

        return valuesPairs;
    }

    /**
     * Finds index at which values array starts
     *
     * @param data see splitByTypeData
     * @return index at which first element of values array starts. If not found returns -1;
     */
    private int findStartOfValuesArray(String data) {
        String startSelector = "data\\\":[";
        return data.indexOf(startSelector) + startSelector.length();
    }

    /**
     * @param data         see splitByTypeData
     * @param currentIndex current index in data string, from which search should be continued
     * @param valuesPairs  linked list for value pairs
     * @return new currentIndex if there can be still further elements, otherwise -1
     */
    private int addNextValuePair(String data, int currentIndex, LinkedList<EnergyDataValuePair> valuesPairs) {
        EnergyDataValuePair energyDataValuePair = new EnergyDataValuePair();

        int energyPairEndIndex = fillValuePairWithData(data, currentIndex, energyDataValuePair);

        valuesPairs.addLast(energyDataValuePair);

        currentIndex = energyPairEndIndex + 2;
        if (data.charAt(currentIndex - 1) != ',') {
            return -1;
        }

        return currentIndex;
    }

    private int fillValuePairWithData(String data, int currentIndex, EnergyDataValuePair valuePair) {
        Double energyValue = 0.0;
        int firstValueEnd = data.indexOf(',', currentIndex + 1);

        Long unixTimeStamp = Long.parseLong(data.substring(currentIndex + 1, firstValueEnd));


        int secondValueStart = firstValueEnd + 1;
        int secondValueEnd = data.indexOf(']', secondValueStart + 1);
        String energyValueString = data.substring(secondValueStart, secondValueEnd);

        // if energyValueString is null, parsing shouldn't be done and 0.0 value be used instead
        if (!energyValueString.equals("null")) {
            energyValue = Double.parseDouble(data.substring(secondValueStart, secondValueEnd));
        }

        valuePair.setEnergyValue(energyValue);
        valuePair.setUnixTimeStamp(unixTimeStamp);
        return secondValueEnd;
    }
}
