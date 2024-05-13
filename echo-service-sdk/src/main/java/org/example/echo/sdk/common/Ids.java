package org.example.echo.sdk.common;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ids {

    @NotEmpty
    private Set<Long> ids;

    public Set<Long> getIds() {
        return ids;
    }
}
