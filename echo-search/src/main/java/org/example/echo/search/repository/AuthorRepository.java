package org.example.echo.search.repository;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.example.common.vo.Result;
import org.example.echo.sdk.author.AuthorVO;
import org.example.echo.sdk.common.Ids;
import org.example.echo.search.converter.AuthorConverter;
import org.example.echo.search.domain.Author;
import org.example.echo.search.feign.AuthorFeign;
import org.example.echo.search.repository.dao.AuthiorEsDao;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class AuthorRepository {

    @Resource
    private AuthiorEsDao authiorEsDao;

    @Resource
    private AuthorFeign authorFeign;


    public void inputAuthor(Author author) throws IOException {
        org.example.echo.search.entity.Author a = AuthorConverter.INSTANCE.convert(author);
        authiorEsDao.inputAuthor(a);
    }

    public List<AuthorVO> searchAuthor(String[] keywords, Integer page, Integer limit) {
        final List<org.example.echo.search.entity.Author> searchResult = authiorEsDao.search(keywords, page, limit);
        final List<AuthorVO> authors = AuthorConverter.INSTANCE.toAuthor(searchResult);
        setAuthorInfo(authors);
        return authors;
    }

    private void setAuthorInfo(List<AuthorVO> authors) {
        if (CollectionUtils.isNotEmpty(authors)) {

            final Set<Long> ids = authors.stream().map(AuthorVO::getId).collect(Collectors.toSet());

            try {
                final Result<List<AuthorVO>> authorFeignByIds = authorFeign.getByIds(new Ids(ids));
                final Map<Long, AuthorVO> authorVOMap = authorFeignByIds.getData().stream().collect(Collectors.toMap(AuthorVO::getId, v -> v, (k1, k2) -> k2));
                authors.forEach(authorVO -> {
                    final AuthorVO author = authorVOMap.get(authorVO.getId());
                    authorVO.setAvatar(author.getAvatar());
                });
            } catch (Exception e) {
                log.error("设置author信息失败 {}", e.getMessage(), e);
            }

        }

    }

}
