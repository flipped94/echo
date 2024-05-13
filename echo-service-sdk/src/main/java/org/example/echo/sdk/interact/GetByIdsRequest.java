package org.example.echo.sdk.interact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetByIdsRequest {
    private Long userId;
    private String biz;
    private List<Long> ids;
}
