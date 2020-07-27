package ds.portal.config.web.filter;

import ds.portal.model.log.AgentInfo;
import ds.portal.model.log.RequestInfo;
import ds.portal.web.request.RequestUtils;
import eu.bitwalker.useragentutils.*;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.function.Predicate;

public class LogbackMdcFilter implements Filter {

    public static final ThreadLocal<RequestInfo> REQUEST_INFO_THREAD_LOCAL = ThreadLocal.withInitial(() -> null);
    private final Predicate<HttpServletRequest> excluded;

    public LogbackMdcFilter(Predicate<HttpServletRequest> excluded) {
        this.excluded = excluded;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;

        if (!excluded.test(req)) {
            UserAgent userAgent = UserAgent.parseUserAgentString(req.getHeader("user-agent"));
            Browser browser = userAgent.getBrowser();
            OperatingSystem operatingSystem = userAgent.getOperatingSystem();

            AgentInfo agentInfo = new AgentInfo();
            agentInfo.setBrowserName(browser.getName());
            agentInfo.setBrowserType(browser.getBrowserType().toString());
            Version browserVersion = userAgent.getBrowserVersion();
            if (browserVersion != null) {
                agentInfo.setBrowserVersion(browserVersion.toString());
            }
            RenderingEngine renderingEngine = browser.getRenderingEngine();
            if (renderingEngine != null) {
                agentInfo.setRenderingEngine(renderingEngine.toString());
            }
            agentInfo.setManufacturer(browser.getManufacturer().toString());
            agentInfo.setOs(operatingSystem.name());
            agentInfo.setDeviceType(operatingSystem.getDeviceType().toString());

            RequestInfo requestInfo = new RequestInfo();
            requestInfo.setMethod(req.getMethod());
            requestInfo.setRequestURI(new UrlPathHelper().getOriginatingRequestUri(req));
            requestInfo.setRequestURL(req.getRequestURL().toString());
            requestInfo.setHeaders(RequestUtils.headers(req));
            requestInfo.setParameters(RequestUtils.parameters(req));
            requestInfo.setAgent(agentInfo);

            REQUEST_INFO_THREAD_LOCAL.set(requestInfo);
        }

        chain.doFilter(request, response);
    }
}
