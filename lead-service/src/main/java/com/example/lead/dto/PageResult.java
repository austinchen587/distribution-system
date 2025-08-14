package com.example.lead.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.List;

@Schema(description = "分页结果")
public class PageResult<T> implements Serializable {
    @Schema(description = "当前页数据项")
    private List<T> list;
    @Schema(description = "总记录数")
    private long total;
    @Schema(description = "当前页码，从1开始")
    private int page;
    @Schema(description = "每页大小")
    private int pageSize;
    @Schema(description = "总页数")
    private int totalPages;

    public PageResult() {}

    public PageResult(List<T> list, long total, int page, int pageSize) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) ((total + pageSize - 1) / pageSize);
    }

    public List<T> getList() { return list; }
    public void setList(List<T> list) { this.list = list; }
    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }
    public int getPage() { return page; }
    public void setPage(int page) { this.page = page; }
    public int getPageSize() { return pageSize; }
    public void setPageSize(int pageSize) { this.pageSize = pageSize; }
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
}

