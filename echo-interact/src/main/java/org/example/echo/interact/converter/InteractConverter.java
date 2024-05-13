package org.example.echo.interact.converter;

import org.example.echo.sdk.interact.Interact;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface InteractConverter {
    InteractConverter INSTANCE = Mappers.getMapper(InteractConverter.class);

    Interact toDomain(org.example.echo.interact.entity.Interact interact);

    List<Interact> toDomain(List<org.example.echo.interact.entity.Interact> interacts);
}
