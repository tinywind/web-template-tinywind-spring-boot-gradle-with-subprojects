package org.tinywind.server.model.form;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.tinywind.server.util.spring.BaseForm;
import org.tinywind.server.util.valid.NotNull;
import lombok.*;

import java.util.UUID;

@NoArgsConstructor
@RequiredArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Data
public class FileForm extends BaseForm {
    @JsonIgnore
    private UUID id;
    @NonNull
    @NotNull
    private String originalName;
    @NonNull
    @NotNull
    private String path;
    @NonNull
    @NotNull
    private Long size;
}
