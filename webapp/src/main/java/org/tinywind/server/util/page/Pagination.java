package org.tinywind.server.util.page;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class Pagination<T> {
    private List<T> rows;
    private int page = 1;
    private int totalCount = 0;
    private int numberOfRowsPerPage = 10;
    private PageNavigation navigation;

    public Pagination(List<T> rows, int page, int totalCount, int numberOfRowsPerPage) {
        set(rows, page, totalCount, numberOfRowsPerPage);
    }

    public Pagination(List<T> rows, int page, int totalCount, int numberOfRowsPerPage, int numberOfRowsPerNavigation) {
        set(rows, page, totalCount, numberOfRowsPerPage, numberOfRowsPerNavigation);
    }

    public void set(List<T> rows, int page, int totalCount, int numberOfRowsPerPage) {
        set(rows, page, totalCount, numberOfRowsPerPage, 10);
    }

    public void set(List<T> rows, int page, int totalCount, int numberOfRowsPerPage, int numberOfRowsPerNavigation) {
        this.rows = rows;
        this.page = page;
        this.totalCount = totalCount;
        this.numberOfRowsPerPage = numberOfRowsPerPage;
        this.navigation = new PageNavigation(page, totalCount, numberOfRowsPerPage, numberOfRowsPerNavigation);
    }

    public void setPage(int page) {
        this.page = page;
        this.navigation = new PageNavigation(page, totalCount, numberOfRowsPerPage, 10);
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        this.navigation = new PageNavigation(page, totalCount, numberOfRowsPerPage, 10);
    }

    public void setNumberOfRowsPerPage(int numberOfRowsPerPage) {
        this.numberOfRowsPerPage = numberOfRowsPerPage;
        this.navigation = new PageNavigation(page, totalCount, numberOfRowsPerPage, 10);
    }
}
