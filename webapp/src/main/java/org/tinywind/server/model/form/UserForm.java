package org.tinywind.server.model.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.tinywind.server.model.enums.UserGrade;
import org.tinywind.server.util.spring.BaseForm;
import org.tinywind.server.util.valid.NotNull;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserForm extends BaseForm {
    @JsonIgnore
    private UUID id;
    @NotNull
    private UserGrade grade;
    @NotNull
    private String loginId;
    @NotNull
    private String password;
    @NotNull
    private String name;
    @NotNull
    private String phone;
    @NotNull
    private String phone2;
    @NotNull
    private String email;
    @NotNull
    private Boolean blackout;
    @NotNull
    private String comment;
}
