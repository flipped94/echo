package org.example.echo.sdk.follow;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 获取某个人的粉丝列表参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetFollowerRequest {
    private int page;
    private int pageSize;
}
