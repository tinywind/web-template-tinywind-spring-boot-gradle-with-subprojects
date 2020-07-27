package org.tinywind.server.service.excel;

import org.tinywind.server.model.User;

import java.util.List;

public class UserExcel extends AbstractExcel {
    public UserExcel(List<User> list) {
        addRow(sheetHeadStyle, "id", "loginId", "createdAt");
        list.forEach(e -> addRow(defaultStyle, e.getId(), e.getLoginId(), niceFormat(e.getCreatedAt())));
    }
}
