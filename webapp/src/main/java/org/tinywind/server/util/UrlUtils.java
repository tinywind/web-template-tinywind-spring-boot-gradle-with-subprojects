package org.tinywind.server.util;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UrlUtils {
    public static Map<String, String> makeParamMap(String... strings) {
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < strings.length; i += 2) {
            map.put(strings[i], strings[i + 1]);
        }
        return map;
    }

    public static String encodeQueryParams(String... strings) {
        return encodeQueryParams(makeParamMap(strings));
    }

    public static String encodeQueryParams(Map<String, ?> params) {
        return encodeQueryParams(convertMapToNameValuePair(params));
    }

    public static String encodeQueryParams(List<NameValuePair> nameValuePairs) {
        return URLEncodedUtils.format(nameValuePairs, "UTF-8");
    }

    public static List<NameValuePair> convertMapToNameValuePair(Map<String, ?> params) {
        return convertMapToNameValuePair(params, false);
    }

    public static List<NameValuePair> convertMapToNameValuePair(Map<String, ?> params, boolean containNullValues) {
        List<NameValuePair> li = new ArrayList<>();
        for (Map.Entry<String, ?> e : params.entrySet()) {
            Object value = e.getValue();
            if (value != null) {
                if (value instanceof List) {
                    //noinspection Convert2streamapi
                    for (Object o : (List) value) {
                        li.add(new BasicNameValuePair(e.getKey(), o.toString()));
                    }
                } else if (value instanceof Map) {
                    ((Map) value).forEach((k, v) -> li.add(new BasicNameValuePair(e.getKey() + "[" + k + "]", v.toString())));
                } else {
                    li.add(new BasicNameValuePair(e.getKey(), value.toString()));
                }
            } else if (containNullValues) {
                li.add(new BasicNameValuePair(e.getKey(), null));
            }
        }
        return li;
    }

    public static String joinQueryString(String url, Map<String, Object> params) {
        return joinQueryString(url, encodeQueryParams(params));
    }

    public static String joinQueryString(String url, String queryParams) {
        if (org.apache.commons.lang3.StringUtils.isEmpty(queryParams)) {
            return url;
        }
        return url + (org.apache.commons.lang3.StringUtils.contains(url, "?") ? "&" : "?") + queryParams;
    }

    public static String encode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String ensureHasProtocol(String url) {
        if (StringUtils.imatches(url, "^https?://.*")) {
            return url;
        }
        return "http://" + url;
    }
}
