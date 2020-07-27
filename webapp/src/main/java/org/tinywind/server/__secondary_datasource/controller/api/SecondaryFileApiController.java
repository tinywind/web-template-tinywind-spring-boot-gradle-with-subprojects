package org.tinywind.server.__secondary_datasource.controller.api;

import org.tinywind.server.model.entity.FileEntity;
import org.tinywind.server.__secondary_datasource.repository.SecondaryFileRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("api/__secondary/files")
public class SecondaryFileApiController {
    private final SecondaryFileRepository repository;

    @GetMapping("")
    public List<FileEntity> files() {
        return repository.findAll();
    }
}
