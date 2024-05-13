package org.example.echo.sdk.interact;

import lombok.Data;

@Data
public class Interact {
    private String biz;
    private Long bizId;
    private Long readCount;
    private Long likeCount;
    private Long collectCount;
    private Long commentCount;
    private Boolean liked;
    private Boolean collected;

    public static Interact defaultX(String biz, Long bizId) {
        Interact interact = new Interact();
        interact.setBiz(biz);
        interact.setBizId(bizId);
        interact.setReadCount(0L);
        interact.setLikeCount(0L);
        interact.setCollectCount(0L);
        interact.setCommentCount(0L);
        interact.setLiked(false);
        interact.setCollected(false);
        return interact;
    }
}
