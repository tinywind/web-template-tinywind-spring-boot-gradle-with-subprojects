package org.tinywind.server.controller.web;

import org.tinywind.server.interceptor.LoginRequired;
import org.tinywind.server.model.form.LoginForm;
import org.tinywind.server.model.search.UserSearch;
import org.tinywind.server.repository.FileRepository;
import org.tinywind.server.repository.UserRepository;
import org.tinywind.server.service.FileService;
import org.tinywind.server.service.excel.UserExcel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import org.tinywind.server.util.StoredFilePaths;
import org.springframework.http.ContentDisposition;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.NoSuchFileException;
import java.io.IOException;

@RequiredArgsConstructor
@Controller
@RequestMapping("")
public class MainController extends PageBaseController {

    private final UserRepository userRepository;
    private final FileRepository fileRepository;

    @Value("${user-data.application.file.location}")
    private String fileLocation;

    @GetMapping("")
    public String loginPage(@ModelAttribute("form") LoginForm form) {
        if (g.isLogin())
            return redirect("main");

        return "login";
    }

    @LoginRequired
    @GetMapping("main")
    public String mainPage(Model model) {
        model.addAttribute("files", fileRepository.findAll());
        return "main";
    }

    // FIXME: 임시
    @SneakyThrows
    @LoginRequired
    @GetMapping("user/download-excel")
    public void downloadExcel(UserSearch search, HttpServletResponse response) {
        search.setLimit(null);
        new UserExcel(userRepository.search(search)).generator(response, "users");
    }

    @GetMapping({FileService.FILE_PATH, FileService.FILE_PATH + "/"})
    public void downloadFile(HttpServletResponse response, @RequestParam(FileService.FILE_REQUEST_PARAM_KEY) String fileName) throws IOException {
        final Path file;
        try {
            StoredFilePaths.validateName(fileName);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        try {
            file = StoredFilePaths.resolve(fileLocation, fileName);
        } catch (IllegalArgumentException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        if (!Files.isRegularFile(file, LinkOption.NOFOLLOW_LINKS)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        try (final java.io.InputStream input = Files.newInputStream(file, LinkOption.NOFOLLOW_LINKS)) {
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", ContentDisposition.attachment()
                    .filename(file.getFileName().toString(), StandardCharsets.UTF_8).build().toString());
            response.setHeader("Content-Transfer-Encoding", "BINARY");
            input.transferTo(response.getOutputStream());
        } catch (NoSuchFileException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
