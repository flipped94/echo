package org.example.echo.article.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.example.common.vo.PageParam;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListReq extends PageParam {

}
