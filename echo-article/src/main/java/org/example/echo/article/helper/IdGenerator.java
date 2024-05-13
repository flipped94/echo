package org.example.echo.article.helper;

import org.example.common.util.Snowflake;
import org.springframework.stereotype.Service;

@Service
public class IdGenerator {
    private final Snowflake snowflake = new Snowflake(2);

    public long nextId() {
        return snowflake.nextId();
    }
}
