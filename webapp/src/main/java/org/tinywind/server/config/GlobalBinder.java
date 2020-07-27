package org.tinywind.server.config;

import org.tinywind.server.model.User;
import org.tinywind.server.util.spring.DateTypePropertyEditor;
import org.tinywind.server.util.spring.TagExtender;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@AllArgsConstructor
@ControllerAdvice
public class GlobalBinder {
    private final TagExtender tagExtender;
    private final RequestMessage requestMessage;
    private final HttpServletRequest request;
    private final RequestGlobal g;

    @ModelAttribute("debugging")
    public Boolean debugging(@Value("${user-data.application.debugging}") Boolean debugging) {
        return Objects.equals(debugging, true);
    }

    @ModelAttribute("version")
    public String version(@Value("${user-data.application.version}") String version) {
        return version;
    }

    @ModelAttribute("contextPath")
    public String contextPath() {
        return request.getContextPath();
    }

    @ModelAttribute("g")
    public RequestGlobal requestGlobal() {
        return g;
    }

    @ModelAttribute("tagExtender")
    public TagExtender tagExtender() {
        return tagExtender;
    }

    @ModelAttribute("message")
    public RequestMessage message() {
        return requestMessage;
    }

    @ModelAttribute("user")
    public User user() {
        return g.getUser();
    }

    @InitBinder
    public void registerCustomEditors(WebDataBinder binder) {
        binder.registerCustomEditor(java.sql.Time.class, new DateTypePropertyEditor<>(java.sql.Time.class));
        binder.registerCustomEditor(java.sql.Timestamp.class, new DateTypePropertyEditor<>(java.sql.Timestamp.class, (t) -> {
            if (t == null) return null;
            return ((java.util.Date) t).getTime() + "";
        }));
        binder.registerCustomEditor(java.sql.Date.class, new DateTypePropertyEditor<>(java.sql.Date.class));
        binder.registerCustomEditor(java.util.Date.class, new DateTypePropertyEditor<>(java.util.Date.class));
    }
}
