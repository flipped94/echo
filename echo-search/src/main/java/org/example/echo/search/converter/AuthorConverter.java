package org.example.echo.search.converter;

import org.example.echo.sdk.author.AuthorEvent;
import org.example.echo.sdk.author.AuthorVO;
import org.example.echo.search.domain.Author;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AuthorConverter {

    AuthorConverter INSTANCE = Mappers.getMapper(AuthorConverter.class);

    @Mapping(source = "authorId",target = "id")
    Author toAuthor(AuthorEvent authorEvent);

    org.example.echo.search.entity.Author convert(Author author);

    List<AuthorVO> toAuthor(List<org.example.echo.search.entity.Author> authors);

    AuthorVO toAuthor(org.example.echo.search.entity.Author author);
}
