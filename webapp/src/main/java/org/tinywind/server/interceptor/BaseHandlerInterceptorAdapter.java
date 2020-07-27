package org.tinywind.server.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.tinywind.server.config.RequestGlobal;
import org.tinywind.server.config.RequestMessage;
import org.tinywind.server.util.JsonResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BaseHandlerInterceptorAdapter extends HandlerInterceptorAdapter {
    @Autowired
    protected RequestGlobal g;
    @Autowired
    protected RequestMessage message;
    @Autowired
    protected ObjectMapper objectMapper;

    protected void redirectMain(HttpServletRequest request, HttpServletResponse response) throws Exception {
        redirect(response, request.getContextPath() + "/");
    }

    protected void redirect(HttpServletResponse response, String url) throws Exception {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(
                "<!doctype html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <meta http-equiv=\"refresh\" content=\"0;url=" + g.escapeQuote(url) + "\" data-state=\"false\">\n" +
                        "    <title>Redirecting</title>\n" +
                        "</head>\n" +
                        "</html>"
        );
    }

    protected void closePopup(HttpServletResponse response, String message) throws IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(
                "<!doctype html>\n" +
                        "<html lang=\"en\">\n" +
                        "<head>\n" +
                        "    <meta charset=\"UTF-8\">\n" +
                        "    <script>" +
                        (StringUtils.isNotEmpty(message) ? "alert('" + g.escapeQuote(message) + "');" : "") +
                        "        (self.opener = self).close();" +
                        "    </script>\n" +
                        "</head>\n" +
                        "</html>"
        );
    }

    protected void sendUnauthorizedJsonResult(HttpServletResponse response) throws IOException {
        sendUnauthorizedJsonResult(response, message.getText(g.isLogin() ? "validation.access.denied" : "validation.login.require"));
    }

    protected void sendUnauthorizedJsonResult(HttpServletResponse response, String message) throws IOException {
        response.setContentType(ContentType.APPLICATION_JSON.toString());
        response.getWriter().write(objectMapper.writeValueAsString(JsonResult.create(message)));
    }
}
