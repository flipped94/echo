package org.example.echo.interact.repository.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.echo.interact.entity.Interact;

import java.util.List;

@Mapper
public interface InteractMapper {

    Long incrReadCount(@Param("biz") String biz, @Param("bizId") Long bizId, @Param("delta") Long delta);

    Long incrCommentCount(@Param("biz") String biz, @Param("bizId") Long bizId, @Param("delta") Long delta);

    Long incrLikeCount(@Param("biz") String biz, @Param("bizId") Long bizId, @Param("delta") Long delta);

    Long incrCollectCount(@Param("biz") String biz, @Param("bizId") Long bizId, @Param("delta") Long delta);

    Interact get(@Param("biz") String biz, @Param("bizId") Long bizId);

    List<Interact> getByBizAndIds(@Param("biz") String biz, @Param("ids") List<Long> ids);

}
