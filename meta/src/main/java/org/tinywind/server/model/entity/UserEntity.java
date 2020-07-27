package org.tinywind.server.model.entity;

import lombok.Data;
import org.tinywind.server.model.enums.UserGrade;

import java.sql.Timestamp;
import java.util.UUID;

@Data
public class UserEntity {
    private UUID id;
    private UserGrade grade;
    private String loginId;
    private String password;
    private String name;
    private String phone;
    private String phone2;
    private String email;
    private Boolean blackout;
    private String comment;
    private Timestamp createdAt;
}
