package org.tinywind.server.util;

import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.io.IOException;

/**
 * ref: https://victorydntmd.tistory.com/342
 */
public class MailHandler {
    private final JavaMailSender sender;
    private final MimeMessage message;
    private final MimeMessageHelper messageHelper;

    public MailHandler(JavaMailSender sender) throws MessagingException {
        this.sender = sender;
        message = sender.createMimeMessage();
        messageHelper = new MimeMessageHelper(message, true, "UTF-8");
    }

    public void setFrom(String fromAddress) throws MessagingException {
        messageHelper.setFrom(fromAddress);
    }

    public void setTo(String email) throws MessagingException {
        messageHelper.setTo(email);
    }

    public void setSubject(String subject) throws MessagingException {
        messageHelper.setSubject(subject);
    }

    public void setText(String text, boolean useHtml) throws MessagingException {
        messageHelper.setText(text, useHtml);
    }

    public void attachResource(String displayFileName, String pathToAttachment) throws MessagingException, IOException {
        final File file = new ClassPathResource(pathToAttachment).getFile();
        messageHelper.addAttachment(displayFileName, new FileSystemResource(file));
    }

    public void attachFile(String displayFileName, File file) throws MessagingException {
        messageHelper.addAttachment(displayFileName, new FileSystemResource(file));
    }

    public void setInlineResourceFile(String contentId, String pathToInline) throws MessagingException, IOException {
        final File file = new ClassPathResource(pathToInline).getFile();
        messageHelper.addInline(contentId, new FileSystemResource(file));
    }

    @SneakyThrows
    public void send() {
        sender.send(message);
    }
}
