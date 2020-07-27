package ds.portal.model.log;

import lombok.Data;

@Data
public class AgentInfo {
    private String browserName;
    private String browserType;
    private String browserVersion;
    private String renderingEngine;
    private String manufacturer;
    private String os;
    private String deviceType;
}
