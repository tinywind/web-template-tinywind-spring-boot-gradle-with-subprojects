package org.tinywind.server.util.page;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author tinywind
 * @since 2016-09-03
 */
public class PageSearch extends PageQueryableSearch {
    @PageQueryable
    protected Integer page = 1;

    @ApiModelProperty("반환 레코드 갯수")
    @PageQueryable
    protected Integer limit = 10;

    public final String getQuery(int toPage) {
        page = toPage;
        return getQuery();
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }
}