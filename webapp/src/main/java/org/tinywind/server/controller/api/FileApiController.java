package org.tinywind.server.controller.api;

import org.tinywind.server.controller.BaseController;
import org.tinywind.server.model.File;
import org.tinywind.server.repository.FileRepository;
import org.tinywind.server.service.FileService;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Slf4j
@RestController
@RequestMapping("api/files")
public class FileApiController extends BaseController {

    private final FileRepository repository;
    private final FileService service;

    @GetMapping("")
    public List<File> files() {
        return repository.findAll();
    }

    @SneakyThrows
    @PostMapping("")
    public File post(@RequestParam MultipartFile file) {
        final UUID fileId = service.save(file);
        return repository.findOne(fileId);
    }
}
