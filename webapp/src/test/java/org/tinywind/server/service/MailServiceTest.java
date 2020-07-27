package org.tinywind.server.service;

import org.tinywind.server.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest(
        properties = {
                "spring.mail.host=",
                "spring.mail.port=",
                "spring.mail.username=",
                "spring.mail.password=",
                "user-data.mail.from=",
        }
)
public class MailServiceTest {
    private static final String TO = "";

    @Autowired
    private MailService service;
    @Autowired
    private FileRepository fileRepository;

    @Test
    public void dummy() {
    }

    // @Test
    public void sendMail() {
        service.messageMail(TO, "test content - html", fileRepository.findAll().stream().findFirst().orElse(null));
    }
}
