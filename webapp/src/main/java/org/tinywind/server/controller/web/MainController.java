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
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

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

    @GetMapping(FileService.FILE_PATH)
    public void downloadFile(HttpServletResponse response, @RequestParam(FileService.FILE_REQUEST_PARAM_KEY) String fileName) throws IOException {
        response.setContentType("application/download; charset=utf-8");
        response.setHeader("Content-Disposition", "attachment; filename=\""
                + URLEncoder.encode(fileName, "UTF-8").replaceAll("[+]", "%20") + "\";");
        response.setHeader("Content-Transfer-Encoding", "BINARY");
        IOUtils.copy(new FileInputStream(new File(fileLocation, fileName)), response.getOutputStream());
    }
}
