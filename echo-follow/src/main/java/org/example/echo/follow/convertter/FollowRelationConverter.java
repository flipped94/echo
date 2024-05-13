package org.example.echo.follow.convertter;

import org.example.common.vo.PageResult;
import org.example.echo.sdk.follow.FollowRelation;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FollowRelationConverter {

    FollowRelationConverter INSTANCE = Mappers.getMapper(FollowRelationConverter.class);

    FollowRelation toDomain(org.example.echo.follow.entity.FollowRelation relation);

    PageResult<FollowRelation> toDomain(PageResult<org.example.echo.follow.entity.FollowRelation> entities);
}
