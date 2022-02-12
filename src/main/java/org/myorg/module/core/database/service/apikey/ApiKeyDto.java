package org.myorg.module.core.database.service.apikey;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.myorg.modules.modules.dto.AbstractDto;

@JsonTypeName("api_key")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = { "id" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiKeyDto implements AbstractDto {

    private long id;

    private String name;

    private String value;

    public static ApiKeyDto create(long id, String name, String value) {
        return ApiKeyDto.builder()
                .id(id)
                .name(name)
                .value(value)
                .build();
    }

}
