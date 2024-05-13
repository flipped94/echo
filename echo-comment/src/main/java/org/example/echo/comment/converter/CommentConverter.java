package org.example.echo.comment.converter;

import org.example.echo.comment.domain.CommentForCreate;
import org.example.echo.comment.domain.CommentVO;
import org.example.echo.comment.domain.ReplyForCreate;
import org.example.echo.comment.domain.ReplyVO;
import org.example.echo.comment.entity.Comment;
import org.example.echo.comment.entity.CommentWithTotalReply;
import org.example.echo.comment.entity.Reply;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface CommentConverter {

    CommentConverter INSTANCE = Mappers.getMapper(CommentConverter.class);

    Comment toEntity(CommentForCreate comment);

    Reply toEntity(ReplyForCreate replyForCreate);


    List<CommentVO> toCommentVO(List<CommentWithTotalReply> comments);

    @Mapping(source = "_id", target = "id")
    CommentVO toCommentVO(CommentWithTotalReply comment);

    List<ReplyVO> toReplyVO(List<Reply> replies);

    ReplyVO toReplyVO(Reply reply);
}
