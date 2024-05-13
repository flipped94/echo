package org.example.echo.sdk.feed;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;


@NoArgsConstructor
@AllArgsConstructor
@Data
public class FeedEvent {
    private Long id;
    private Long uid;
    private String type;
    private Long createTime;
    private Map<String, String> ext;

    public FeedEvent(Long uid, String type, Long createTime, Map<String, String> ext) {
        this.uid = uid;
        this.type = type;
        this.createTime = createTime;
        this.ext = ext;
    }
}
