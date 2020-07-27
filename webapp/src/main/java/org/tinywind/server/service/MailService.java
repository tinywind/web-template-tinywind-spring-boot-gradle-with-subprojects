package org.tinywind.server.service;

import org.tinywind.server.model.File;
import org.tinywind.server.util.MailHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import javax.servlet.ServletContext;

@Slf4j
@Service
public class MailService extends BaseService {
    private final JavaMailSender mailSender;
    private final FileService fileService;
    private final TemplateEngine templateEngine;

    @Value("${user-data.mail.from}")
    private String from;

    public MailService(JavaMailSender mailSender, FileService fileService, final ServletContext servletContext) {
        this.mailSender = mailSender;
        this.fileService = fileService;

        final ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/mail/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(3600000L);
        templateResolver.setCacheable(true);
        templateResolver.setCharacterEncoding("UTF-8");

        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    @SneakyThrows
    public void messageMail(String address, String content, File file) {
        final MailHandler mailHandler = new MailHandler(mailSender);

        final Context context = new Context();
        context.setVariable("g", g);
        context.setVariable("message", message);
        context.setVariable("content", content);

        mailHandler.setTo(address);
        mailHandler.setSubject("MESSAGE");
        mailHandler.setFrom(from);
        mailHandler.setText(templateEngine.process("message", context), true);

        if (file != null)
            mailHandler.attachFile(file.getOriginalName(), fileService.file(file.getPath()));

        mailHandler.send();
    }
}
