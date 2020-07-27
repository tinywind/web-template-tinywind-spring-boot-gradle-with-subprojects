package org.tinywind.server.interceptor;

import org.tinywind.server.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author tinywind
 * @since 2016-09-03
 */
@Slf4j
@Component
public class LoginRequireInterceptor extends AnnotationHandlerInterceptorAdapter<LoginRequired> {
    public LoginRequireInterceptor() {
        super(LoginRequired.class);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler, LoginRequired annotation) throws Exception {
        final User user = g.getUser();
        if (user == null)
            return processFail(request, response, annotation);

        return true;
    }

    private boolean processFail(HttpServletRequest request, HttpServletResponse response, LoginRequired annotation) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        if (annotation.type().equals(LoginRequired.Type.API)) {
            sendUnauthorizedJsonResult(response);
            return false;
        }

        if (annotation.type().equals(LoginRequired.Type.POPUP)) {
            closePopup(response, message.getText(g.isLogin() ? "error.access.denied" : "error.login.require"));
            return false;
        }

        g.alert(g.isLogin() ? "error.access.denied" : "error.login.require");
        redirectMain(request, response);
        return false;
    }
}
