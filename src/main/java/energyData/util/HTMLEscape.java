package energyData.util;

import org.springframework.stereotype.Component;

@Component
public class HTMLEscape {
    /**
     * Removes with regex all html tags from string.
     *
     * @param s initial string
     * @return new string
     */
    public static String removeAllHtmlTags(String s) {
        return s.replaceAll("<[^>]*>", "");
    }
}
