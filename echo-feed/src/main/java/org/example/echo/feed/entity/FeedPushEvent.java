package org.example.echo.feed.entity;

import com.alibaba.nacos.shaded.com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.echo.sdk.feed.FeedEvent;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeedPushEvent {

    private Long id;

    private Long uid;

    private String type;

    private String content;

    private Long createTime;


    public static FeedPushEvent feedPushEvent(FeedEvent event) {
        final String s = new Gson().toJson(event.getExt());
        return new FeedPushEvent((event.getUid()), event.getType(), s, event.getCreateTime());
    }

    private FeedPushEvent(Long uid, String type, String content, Long createTime) {
        this.uid = uid;
        this.type = type;
        this.content = content;
        this.createTime = createTime;
    }
}
