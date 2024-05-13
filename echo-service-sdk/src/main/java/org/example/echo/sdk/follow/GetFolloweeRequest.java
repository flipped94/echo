package org.example.echo.sdk.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取某个人的关注列表参数
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class GetFolloweeRequest {
    private int page;
    private int pageSize;
}
