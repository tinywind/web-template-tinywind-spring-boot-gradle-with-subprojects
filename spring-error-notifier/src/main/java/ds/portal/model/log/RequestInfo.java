package ds.portal.model.log;

import lombok.Data;

import java.util.Map;

@Data
public class RequestInfo {
    private AgentInfo agent;
    private String method;
    private String requestURI;
    private String requestURL;
    private Map<String, String> headers;
    private Map<String, String> parameters;
}
