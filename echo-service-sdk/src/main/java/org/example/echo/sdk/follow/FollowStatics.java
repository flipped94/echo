package org.example.echo.sdk.follow;

import lombok.Data;

@Data
public class FollowStatics {
    // 被多少人关注
    private Long followers;
    // 关注了多少人
    private Long followees;
}
