package org.example.echo.comment.service;

import jakarta.annotation.Resource;
import org.example.echo.comment.domain.CommentForCreate;
import org.example.echo.comment.domain.CommentVO;
import org.example.echo.comment.domain.ReplyVO;
import org.example.echo.comment.domain.ReplyForCreate;
import org.example.echo.comment.repository.CommentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    @Resource
    private CommentRepository commentRepository;

    public Long createComment(CommentForCreate commentForCreate) {
        return commentRepository.createComment(commentForCreate);
    }

    public Long createReply(ReplyForCreate replyForCreate) {
        return commentRepository.createReply(replyForCreate);
    }

    public List<CommentVO> getMoreComments(String biz, Long bizId, Long minCommentId, int limit) {
        return commentRepository.getMoreComments(biz, bizId, minCommentId, limit);
    }

    public List<ReplyVO> getMoreRely(String biz, Long bizId, Long rootCommentId, Long minReplyId) {
        return commentRepository.getMoreRely(biz, bizId, rootCommentId, minReplyId);
    }

}
