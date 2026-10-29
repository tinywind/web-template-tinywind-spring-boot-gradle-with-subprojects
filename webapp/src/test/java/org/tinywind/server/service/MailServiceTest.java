package org.tinywind.server.service;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;
import jakarta.mail.Multipart;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.tinywind.server.model.File;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class MailServiceTest {
    @TempDir Path temporary;

    private void collectParts(jakarta.mail.Part part, java.util.List<jakarta.mail.Part> parts) throws Exception {
        parts.add(part);
        if (part.getContent() instanceof Multipart multipart) {
            for (int i = 0; i < multipart.getCount(); i++)
                collectParts(multipart.getBodyPart(i), parts);
        }
    }

    @Test void evaluatesConditionsAndMethodsWithTheMailTemplateEngine() {
        JavaMailSenderImpl sender = new JavaMailSenderImpl();
        MailService service = new MailService(sender, new FileService(null),
                new MockServletContext("src/main/webapp", new FileSystemResourceLoader()));
        var engine = (org.thymeleaf.TemplateEngine) ReflectionTestUtils.getField(service, "templateEngine");
        engine.setTemplateResolver(new org.thymeleaf.templateresolver.StringTemplateResolver());
        var context = new org.thymeleaf.context.Context();
        context.setVariable("content", "Alice");
        String html = engine.process("<p th:if=\"${content != null}\" th:text=\"${content.substring(1)}\"></p>", context);
        assertTrue(html.contains(">lice</p>"), html);
    }

    @Test void rendersAndDeliversMailWithAttachmentToLoopbackServer() throws Exception {
        GreenMail smtp = new GreenMail(new ServerSetup(0, "127.0.0.1", ServerSetup.PROTOCOL_SMTP));
        smtp.start();
        try {
            JavaMailSenderImpl sender = new JavaMailSenderImpl();
            sender.setHost("127.0.0.1"); sender.setPort(smtp.getSmtp().getPort());
            FileService files = new FileService(null);
            ReflectionTestUtils.setField(files, "fileLocation", temporary.toString());
            Files.writeString(temporary.resolve("attachment.txt"), "attachment-content");
            File attachment = new File();
            attachment.setOriginalName("report.txt"); attachment.setPath("attachment.txt");
            MailService service = new MailService(sender, files,
                    new MockServletContext("src/main/webapp", new FileSystemResourceLoader()));
            ReflectionTestUtils.setField(service, "from", "sender@example.invalid");
            service.messageMail("recipient@example.invalid", "<script>unsafe</script>", attachment);
            assertTrue(smtp.waitForIncomingEmail(5000, 1));
            var message = smtp.getReceivedMessages()[0];
            assertEquals("MESSAGE", message.getSubject());
            java.util.List<jakarta.mail.Part> parts = new java.util.ArrayList<>();
            collectParts(message, parts);
            var html = parts.stream().filter(part -> {
                try { return part.isMimeType("text/html"); } catch (jakarta.mail.MessagingException e) { throw new AssertionError(e); }
            }).findFirst().orElseThrow();
            assertTrue(html.getContent().toString().contains("&lt;script&gt;unsafe&lt;/script&gt;"));
            var attached = parts.stream().filter(part -> {
                try { return "report.txt".equals(part.getFileName()); } catch (jakarta.mail.MessagingException e) { throw new AssertionError(e); }
            }).findFirst().orElseThrow();
            assertEquals("attachment-content", new String(attached.getInputStream().readAllBytes(), java.nio.charset.StandardCharsets.UTF_8));
        } finally { smtp.stop(); }
    }
}
