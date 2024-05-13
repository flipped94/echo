package org.example.echo.author.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.example.echo.author.entity.Author;

import java.util.List;
import java.util.Set;

@Mapper
public interface AuthorMapper {

    Integer save(Author author);

    Author getByEmail(String email);

    Author getById(Long id);

    List<Author> getByIds(@Param("ids") Set<Long> ids);

    Integer updateBaseInfo(@Param("author") Author author);

    Integer updatePassword(@Param("id") Long id,@Param("newPassword") String newPassword);
}
