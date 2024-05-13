package org.example.echo.interact.repository;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Random;

@SpringBootTest
public class InteractRepositoryTest {

    @Resource
    private InteractRepository interactRepository;

    @Test
    public void testArticleReRank() {
        final long i = new Random(System.currentTimeMillis()).nextLong(10);
        interactRepository.rank(i);
    }
}
