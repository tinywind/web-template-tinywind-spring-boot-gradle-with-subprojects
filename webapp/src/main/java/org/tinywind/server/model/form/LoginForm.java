package org.tinywind.server.model.form;

import org.tinywind.server.util.spring.BaseForm;
import org.tinywind.server.util.valid.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginForm extends BaseForm {
    @NotNull
    private String loginId;
    @NotNull
    private String password;
}
