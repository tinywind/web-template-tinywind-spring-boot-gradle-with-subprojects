package ds.portal.web.request;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

public class RequestUtils {

    private RequestUtils() {
    }

    public static Map<String, String> headers(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();

        Enumeration<String> headerMap = request.getHeaderNames();
        while (headerMap.hasMoreElements()) {
            String name = headerMap.nextElement();
            String value = request.getHeader(name);
            map.put(name, value);
        }
        return map;
    }

    public static Map<String, String> parameters(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();

        for (String key : parameterMap.keySet()) {
            String[] values = parameterMap.get(key);
            StringJoiner valueString = new StringJoiner(",");

            for (String value : values) {
                valueString.add(value);
            }

            map.put(key, valueString.toString());
        }
        return map;
    }

}
