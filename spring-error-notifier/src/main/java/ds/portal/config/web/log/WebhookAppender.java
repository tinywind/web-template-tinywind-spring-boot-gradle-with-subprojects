package ds.portal.config.web.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.core.UnsynchronizedAppenderBase;
import com.fasterxml.jackson.databind.ObjectMapper;
import ds.portal.config.properties.LogConfigProperties;
import ds.portal.config.web.filter.LogbackMdcFilter;
import ds.portal.model.log.ErrorLog;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WebhookAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper;
    private final LogConfigProperties logConfigProperties;

    public WebhookAppender(LogConfigProperties logConfigProperties, ObjectMapper mapper) {
        this.logConfigProperties = logConfigProperties;
        this.mapper = mapper;
    }

    @Override
    protected void append(ILoggingEvent eventObject) {
        if (!eventObject.getLevel().isGreaterOrEqual(logConfigProperties.getLevel())) {
            return;
        }

        ErrorLog errorLog = new ErrorLog();
        errorLog.setService(logConfigProperties.getService());
        errorLog.setLoggerName(eventObject.getLoggerName());
        errorLog.setLevel(eventObject.getLevel().toString());
        errorLog.setThreadName(eventObject.getThreadName());
        errorLog.setFormattedMessage(eventObject.getFormattedMessage());
        errorLog.setCreatedAt(eventObject.getTimeStamp());

        StackTraceElement[] callerDatas = eventObject.getCallerData();
        if (callerDatas != null && callerDatas.length > 0 && callerDatas[0] != null) {
            errorLog.setCaller(toStackTrace(callerDatas[0]));
        }

        IThrowableProxy throwableProxy = eventObject.getThrowableProxy();
        if (throwableProxy != null) {
            ErrorLog.Throwable throwable = new ErrorLog.Throwable();
            throwable.setClassName(throwableProxy.getClassName());
            throwable.setMessage(throwableProxy.getMessage());

            List<ErrorLog.StackTrace> collect = Arrays.stream(throwableProxy.getStackTraceElementProxyArray())
                    .map(step -> toStackTrace(step.getStackTraceElement())).collect(Collectors.toList());
            throwable.setStackTraces(collect);

            errorLog.setThrowable(throwable);
        }

        send(errorLog);
    }

    private ErrorLog.StackTrace toStackTrace(StackTraceElement ste) {
        ErrorLog.StackTrace stackTrace = new ErrorLog.StackTrace();
        stackTrace.setClassName(ste.getClassName());
        stackTrace.setMethodName(ste.getMethodName());
        stackTrace.setFileName(ste.getFileName());
        stackTrace.setLineNumber(ste.getLineNumber());
        return stackTrace;
    }

    private void send(ErrorLog errorLog) {
        try {
            errorLog.setRequestInfo(LogbackMdcFilter.REQUEST_INFO_THREAD_LOCAL.get());
            String json = mapper.writeValueAsString(errorLog);

            //noinspection ConstantConditions
            if (false) {
                return;
            }

            Request request = new Request.Builder()
                    .url(logConfigProperties.getEndpointUrl())
                    .post(RequestBody.create(MediaType.parse("application/json"), json))
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    System.out.println("error + Connect Server Error is " + e.toString());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    // ignore
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

