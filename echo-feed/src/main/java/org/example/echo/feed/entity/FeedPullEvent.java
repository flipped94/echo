package org.example.echo.feed.entity;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo.sdk.feed.FeedEvent;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeedPullEvent {

    private Long id;

    private Long uid;

    private String type;

    private String content;

    private Long createTime;

    private Long updateTime;

    public static FeedPullEvent feedPullEvent(FeedEvent event) {
        final String s = new Gson().toJson(event.getExt());
        return new FeedPullEvent((event.getUid()), event.getType(), s, event.getCreateTime(), event.getCreateTime());
    }

    private FeedPullEvent(Long uid, String type, String content, Long createTime, Long updateTime) {
        this.uid = uid;
        this.type = type;
        this.content = content;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
}
