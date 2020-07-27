package org.tinywind.server.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.tinywind.server.model.entity.UserEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public class User extends UserEntity {
    @JsonIgnore
    @Override
    public String getPassword() {
        return super.getPassword();
    }
}
