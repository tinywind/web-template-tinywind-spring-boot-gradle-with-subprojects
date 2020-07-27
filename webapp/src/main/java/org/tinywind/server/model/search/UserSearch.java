package org.tinywind.server.model.search;

import org.tinywind.server.util.page.PageSearch;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class UserSearch extends PageSearch {
    private String loginId;
}
