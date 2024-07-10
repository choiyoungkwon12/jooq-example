package com.sight.jooqfirstlook.web;

public class PagedResponse {
    private long page;
    private long pageSize;


    public PagedResponse(long page, long pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

    public PagedResponse() {
    }

    public long getPage() {
        return page;
    }

    public long getPageSize() {
        return pageSize;
    }
}
