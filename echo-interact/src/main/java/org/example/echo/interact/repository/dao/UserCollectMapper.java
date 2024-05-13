package org.example.echo.interact.repository.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.echo.interact.entity.UserCollect;

@Mapper
public interface UserCollectMapper {
    int upsert(
            @Param("biz") String biz,
            @Param("bizId") Long bizId,
            @Param("userId") Long userId,
            @Param("cid") Long cid,
            @Param("status") Integer status);

    UserCollect getCollectInfo(
            @Param("biz") String biz,
            @Param("bizId") Long bizId,
            @Param("userId") Long userId,
            @Param("status") Integer status);
}
