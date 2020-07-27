package org.tinywind.server.__secondary_datasource.repository;

import org.tinywind.server.model.entity.FileEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecondaryFileRepository {
    List<FileEntity> findAll();
}