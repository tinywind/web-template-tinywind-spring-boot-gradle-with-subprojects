package org.tinywind.server.repository;

import org.springframework.stereotype.Repository;
import org.tinywind.server.model.File;
import org.tinywind.server.model.form.FileForm;

import java.util.List;
import java.util.UUID;

@Repository
public interface FileRepository {
    List<File> findAll();

    File findOne(UUID fileId);

    void insert(FileForm form);
}