package org.tinywind.server.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;
import org.tinywind.server.controller.web.MainController;
import org.tinywind.server.repository.FileRepository;
import org.tinywind.server.service.FileService;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FileAccessSecurityTest {
    @TempDir Path temporary;

    @Test void rejectsTraversalAndControlCharactersBeforeWriting() throws Exception {
        Path root = Files.createDirectory(temporary.resolve("files"));
        FileRepository repository = mock(FileRepository.class);
        FileService service = new FileService(repository);
        ReflectionTestUtils.setField(service, "fileLocation", root.toString());
        for (String name : new String[]{"../private.txt", "/private.txt", "..\\private.txt", "bad\r\nname", ".", ".."}) {
            assertThrows(IllegalArgumentException.class, () -> service.save(name, new byte[]{1}), name);
        }
        verifyNoInteractions(repository);
        try (var files = Files.list(root)) { assertEquals(0, files.count()); }
    }

    @Test void downloadsUnicodeButRejectsTraversalAndSymlinks() throws Exception {
        Path root = Files.createDirectory(temporary.resolve("files"));
        Files.writeString(root.resolve("report 한글.txt"), "stored-content");
        Path outside = Files.writeString(temporary.resolve("private.txt"), "private");
        Files.createSymbolicLink(root.resolve("linked.txt"), outside);
        MainController controller = new MainController(null, null);
        ReflectionTestUtils.setField(controller, "fileLocation", root.toString());
        MockHttpServletResponse response = new MockHttpServletResponse();
        controller.downloadFile(response, "report 한글.txt");
        assertEquals("stored-content", response.getContentAsString());
        assertFalse(response.getHeader("Content-Disposition").contains("\r"));
        for (String name : new String[]{"../private.txt", outside.toString(), "..\\private.txt", "bad\r\nname"}) {
            MockHttpServletResponse rejected = new MockHttpServletResponse();
            controller.downloadFile(rejected, name);
            assertEquals(400, rejected.getStatus(), name);
            assertEquals(0, rejected.getContentAsByteArray().length);
        }
        MockHttpServletResponse symlink = new MockHttpServletResponse();
        controller.downloadFile(symlink, "linked.txt");
        assertEquals(404, symlink.getStatus());
        assertEquals(0, symlink.getContentAsByteArray().length);
    }

    @Test void fileLookupRejectsSymbolicLinks() throws Exception {
        Path root = Files.createDirectory(temporary.resolve("files"));
        Path outside = Files.writeString(temporary.resolve("private.txt"), "private");
        Files.createSymbolicLink(root.resolve("linked.txt"), outside);
        FileService service = new FileService(mock(FileRepository.class));
        ReflectionTestUtils.setField(service, "fileLocation", root.toString());
        assertThrows(IllegalArgumentException.class, () -> service.file("linked.txt"));
        assertThrows(IllegalArgumentException.class, () -> service.file("../private.txt"));
    }
}
