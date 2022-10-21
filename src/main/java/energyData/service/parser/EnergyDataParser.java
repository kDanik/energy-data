package energyData.service.parser;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class EnergyDataParser {
    /**
     * Parses request body with energy data
     * @param requestData
     * @return List with EnergyData (names and values)
     */
    public List<EnergyData> parseEnergyDataString(String requestData) {
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

        int start = requestData.indexOf(startSearchString) + startSearchString.length();
        int end = requestData.lastIndexOf(endSearchString) - endSearchString.length() + 1;

        return requestData.substring(start, end);
    }

    /**
     * Splits string with js content of request body by their type and return string array (without first element!)
     *
     * @param jsContent String with js content of request body
     * @return String array
     */
    private String[] splitByType(String jsContent) {
        String[] temp = jsContent.split("name\\\\\":");
        return Arrays.copyOfRange(temp, 1, temp.length);
    }

    /**
     * Parses one string from array {@link this.splitByType()}, getting its name and values
     *
     * @param splitByTypeData one splitByType string from splitByType array {@link this.splitByType()}
     * @return EnergyData with parse result
     */
    private EnergyData extractData(String splitByTypeData) {
        EnergyData energyData = new EnergyData();

        energyData.setName(removeAllHtmlTags(extractName(splitByTypeData)));
        energyData.setData(extractValuesPairs(splitByTypeData));

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
     * @return List with extracted values (EnergyDataValuePair)
     */
    private List<EnergyDataValuePair> extractValuesPairs(String data) {
        ArrayList<EnergyDataValuePair> valuesPairs = new ArrayList<>();

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
     * @param valuesPairs  array in which valuePair should be added, if found
     * @return new currentIndex if there can be still elements, otherwise -1
     */
    private int addNextValuePair(String data, int currentIndex, ArrayList<EnergyDataValuePair> valuesPairs) {
        int firstValueEnd = data.indexOf(',', currentIndex + 1);
        Long firstValue = Long.parseLong(data.substring(currentIndex + 1, firstValueEnd));

        int secondValueStart = firstValueEnd + 1;
        int secondValueEnd = data.indexOf(']', secondValueStart + 1);
        Double secondValue = Double.parseDouble(data.substring(secondValueStart, secondValueEnd));

        valuesPairs.add(new EnergyDataValuePair(firstValue, secondValue));

        currentIndex = secondValueEnd + 2;
        if (data.charAt(currentIndex - 1) != ',') {
            return -1;
        }

        return currentIndex;
    }

    /**
     * Removes all html elements with regex from string. TODO This can go into separate class
     *
     * @param s initial string
     * @return new string
     */
    private String removeAllHtmlTags(String s) {
        return s.replaceAll("<[^>]*>", "");
    }
}
