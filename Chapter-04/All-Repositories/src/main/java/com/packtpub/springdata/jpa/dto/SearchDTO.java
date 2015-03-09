package com.packtpub.springdata.jpa.dto;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * @author Petri Kainulainen
 */
public class SearchDTO {

    private int pageIndex;

    private int pageSize;

    private String searchTerm;

    public SearchDTO() {

    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }
}
