package org.tinywind.server.config;

import org.tinywind.server.model.User;
import org.tinywind.server.service.storage.SessionStorage;
import org.tinywind.server.util.spring.SpringApplicationContextAware;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

@Slf4j
@AllArgsConstructor
@Component
public class RequestGlobal {
    private static final String REQUEST_GLOBAL_CURRENT_USER = "REQUEST_GLOBAL_CURRENT_USER";
    private static final String REQUEST_GLOBAL_ALERTS = "REQUEST_GLOBAL_ALERTS";

    private final HttpSession session;
    private final SessionStorage sessionStorage;

    public User getUser() {
        return sessionStorage.get(session.getId(), REQUEST_GLOBAL_CURRENT_USER, User.class);
    }

    public boolean isLogin() {
        return getUser() != null;
    }

    public void setCurrentUser(User user) {
        sessionStorage.set(session.getId(), REQUEST_GLOBAL_CURRENT_USER, user);
    }

    public void invalidateSession() {
        sessionStorage.expire(session.getId());
    }

    public void alert(String code, Object... objects) {
        final List<String> alerts = getAlerts();
        alerts.add(SpringApplicationContextAware.requestMessage().getText(code, objects));
        sessionStorage.set(session.getId(), REQUEST_GLOBAL_ALERTS, alerts);
    }

    public void alertString(String string) {
        final List<String> alerts = getAlerts();
        alerts.add(string);
        sessionStorage.set(session.getId(), REQUEST_GLOBAL_ALERTS, alerts);
    }

    @SuppressWarnings("unchecked")
    public List<String> getAlerts() {
        final List<String> alerts = sessionStorage.get(session.getId(), REQUEST_GLOBAL_ALERTS, List.class, ArrayList::new);
        return new ArrayList<>(alerts);
    }

    public List<String> popAlerts() {
        final List<String> alerts = getAlerts().stream().distinct().collect(Collectors.toList());
        sessionStorage.remove(session.getId(), REQUEST_GLOBAL_ALERTS);
        return alerts;
    }

    public String urlEncode(String s) throws UnsupportedEncodingException {
        return URLEncoder.encode(s, "UTF-8");
    }

    public String now() {
        return now("yyyy-MM-dd");
    }

    public String now(String format) {
        return dateFormat(new Date(System.currentTimeMillis()), format);
    }

    public String dateFormat(Date date, String format) {
        if (date == null)
            return null;

        final DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return dateFormat.format(date);
    }

    public String dateFormat(Date date) {
        return dateFormat(date, "yyyy-MM-dd");
    }

    public String timestampFormat(Date date, String format) {
        if (date == null)
            return null;

        final DateFormat dateFormat = new SimpleDateFormat(format);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        return dateFormat.format(date);
    }

    public String timestampFormat(Date date) {
        return dateFormat(date, "yyyy-MM-dd HH:mm:ss");
    }

    public String escapeQuote(String text) {
        return text
                .replaceAll("'", "\\\\'")
                .replaceAll("\"", "\\\\\"")
                .replaceAll("[\n]", "\\\\n")
                .replaceAll("[\r]", "\\\\r");
    }

    public String htmlQuote(String text, String defaultStr) {
        if (StringUtils.isEmpty(text))
            return defaultStr;
        return text.replaceAll("\"", "&quot;").replaceAll("'", "&#39;").replaceAll("<", "&lt;");
    }

    public String htmlQuote(final String text) {
        return htmlQuote(text, "");
    }

    public String addQueryString(String url, String query) {
        if (!url.contains("?"))
            return url + "?" + query;
        else if (!query.startsWith("&"))
            return url + "&" + query;
        else
            return url + query;
    }

    public String timeFormatFromSeconds(long sec) {
        return timeFormatFromSeconds(sec, "HH:mm:ss");
    }

    public String timeFormatFromSeconds(long sec, String timeFormat) {
        final Date date = new Date(sec * 1000);
        final SimpleDateFormat dateFormat = new SimpleDateFormat(timeFormat);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }
}
