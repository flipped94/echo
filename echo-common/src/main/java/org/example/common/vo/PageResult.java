package org.example.common.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {
    private int pageNum;
    private int pageSize;
    private long total;
    private int pages;
    private List<T> data;
}
