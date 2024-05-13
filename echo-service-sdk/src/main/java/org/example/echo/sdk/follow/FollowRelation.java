package org.example.echo.sdk.follow;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FollowRelation {
    // 关注的人
    @JsonSerialize(using = ToStringSerializer.class)
    private Long follower;

    // 被关注的人
    @JsonSerialize(using = ToStringSerializer.class)
    private Long followee;
}
