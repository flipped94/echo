package org.example.echo.sdk.mail;

import lombok.Data;

@Data
public class EmailEvent {

    private String from;
    private String to;
    private String topic;
}
