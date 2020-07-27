package org.tinywind.server.util.spring;

import org.tinywind.server.util.RequestUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Component
public class TagExtender {
    private final HttpServletRequest httpServletRequest;

    public void stackBody(String sourceName, String bucketName) {
        final String body = (String) httpServletRequest.getAttribute(sourceName);
        if (body == null) {
            return;
        }
        httpServletRequest.removeAttribute(sourceName);
        getBody(bucketName).add(body);
    }

    public List<String> getBody(String bucketName) {
        return RequestUtils.getAttribute(bucketName, ArrayList::new);
    }

    public List<String> getScriptsTags() {
        return getBody("TAG_SCRIPTS_LIST");
    }
}
