package org.tinywind.server.model.entity;

import lombok.Data;

import java.sql.*;
import org.tinywind.server.model.enums.*;
import java.util.UUID;

@Data
public class FileEntity {
    private UUID id;
    private String originalName;
    private String path;
    private String size;
    private Timestamp createdAt;
}
