package org.example.echo.comment.controller;

import jakarta.annotation.Resource;
import org.example.common.annotation.LoginNotRequired;
import org.example.common.vo.Result;
import org.example.echo.comment.domain.CommentForCreate;
import org.example.echo.comment.domain.CommentVO;
import org.example.echo.comment.domain.ReplyVO;
import org.example.echo.comment.domain.ReplyForCreate;
import org.example.echo.comment.service.CommentService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/comments")
@RestController
public class CommentController {

    @Resource
    private CommentService commentService;

    @PostMapping("/comment")
    public Result<Long> createComment(@RequestBody @Validated CommentForCreate commentForCreate) {
        Long id = commentService.createComment(commentForCreate);
        return Result.success(id);
    }

    @PostMapping("/reply")
    public Result<Long> createReply(@RequestBody @Validated ReplyForCreate replyForCreate) {
        Long id = commentService.createReply(replyForCreate);
        return Result.success(id);
    }

    @LoginNotRequired
    @GetMapping("/more-comments")
    public Result<List<CommentVO>> getMoreComments(
            @RequestParam("biz") String biz,
            @RequestParam("bizId") Long bizId,
            @RequestParam("minCommentId") Long minCommentId,
            @RequestParam("limit") int limit) {
        final List<CommentVO> comments = commentService.getMoreComments(biz, bizId, minCommentId, limit);
        return Result.success(comments);
    }

    @LoginNotRequired
    @GetMapping("/more-reply")
    public Result<List<ReplyVO>> getMoreReply(
            @RequestParam("biz") String biz,
            @RequestParam("bizId") Long bizId,
            @RequestParam("rootCommentId") Long rootCommentId,
            @RequestParam("minReplyId") Long minReplyId) {
        final List<ReplyVO> replies = commentService.getMoreRely( biz,  bizId,  rootCommentId,  minReplyId);
        return Result.success(replies);
    }
}
