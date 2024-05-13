package org.example.echo.author.converter;

import org.example.echo.author.domain.AuthorForUpdate;
import org.example.echo.author.entity.Author;
import org.example.echo.sdk.author.AuthorVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface AuthorConverter {

    AuthorConverter INSTANCE = Mappers.getMapper(AuthorConverter.class);


    AuthorVO toAuthor(Author author);


    List<AuthorVO> toAuthor(List<Author> author);

    Author toAuthor(AuthorForUpdate authorForUpdate);
}
