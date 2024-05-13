package org.example.echo.comment.helper;

import org.example.common.util.Snowflake;
import org.springframework.stereotype.Service;

@Service
public class IdGenerator {
    private final Snowflake snowflake = new Snowflake(1);

    public long nextId() {
        return snowflake.nextId();
    }
}
