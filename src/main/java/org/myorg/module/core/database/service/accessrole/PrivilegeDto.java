package org.myorg.module.core.database.service.accessrole;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.module.core.database.domainobject.PrivilegeEmbeddable;
import org.myorg.modules.modules.dto.AbstractDto;

@JsonTypeName("privilege")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = { "key" })
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PrivilegeDto implements AbstractDto {

    private String key;

    private AccessOp[] ops;

    public static PrivilegeDto from(PrivilegeEmbeddable privilege) {
        if (privilege == null) {
            return null;
        }

        return PrivilegeDto.builder()
                .key(privilege.getKey())
                .ops(privilege.getValue())
                .build();
    }

}
