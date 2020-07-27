package org.tinywind.server.model.entity;

import lombok.Data;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class UserAuthorizationCodeEntity {
    private UUID user;
    private String code;
    private Timestamp expiringTime;
    private Timestamp createdAt;
}
