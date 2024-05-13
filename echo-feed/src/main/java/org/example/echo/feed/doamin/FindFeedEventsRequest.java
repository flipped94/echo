package org.example.echo.feed.doamin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FindFeedEventsRequest {
    private int limit;
    private long timestamp;
}
