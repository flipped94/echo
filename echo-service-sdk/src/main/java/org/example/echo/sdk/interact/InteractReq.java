package org.example.echo.sdk.interact;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class InteractReq {
    private String biz;
    private Long bizId;
    private Long userId;
}
