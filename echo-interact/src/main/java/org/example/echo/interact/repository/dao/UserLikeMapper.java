package org.example.echo.interact.repository.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.echo.interact.entity.UserLike;

@Mapper
public interface UserLikeMapper {

    int upsert(
            @Param("biz") String biz,
            @Param("bizId") Long bizId,
            @Param("userId") Long userId,
            @Param("status") Integer status);

    UserLike GetLikeInfo(
            @Param("biz") String biz,
            @Param("bizId") Long bizId,
            @Param("userId") Long userId,
            @Param("status") Integer status);
}
