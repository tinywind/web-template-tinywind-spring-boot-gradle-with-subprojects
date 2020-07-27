package ds.portal.model.log;

import lombok.Data;

import java.util.List;

@Data
public class ErrorLog {
    private String service;
    private String loggerName;
    private String level;
    private String threadName;
    private String formattedMessage;
    private StackTrace caller;
    private Throwable throwable;
    private Long createdAt;
    private RequestInfo requestInfo;

    @Data
    public static class Throwable {
        private String className;
        private String message;
        private List<StackTrace> stackTraces;
    }

    @Data
    public static class StackTrace {
        private String className;
        private String methodName;
        private String fileName;
        private Integer LineNumber;
    }

}