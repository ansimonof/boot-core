package org.myorg.module.core.database.service.accessrole;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.module.core.database.domainobject.PrivilegeEmbeddable;
import org.myorg.modules.modules.dto.AbstractDto;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@JsonTypeName("access_role")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = { "id" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessRoleDto implements AbstractDto {

    private long id;

    private String name;

    private Set<PrivilegeDto> privileges;

    public static AccessRoleDto from(DbAccessRole dbAccessRole) {
        if (dbAccessRole == null) {
            return null;
        }

        Set<PrivilegeEmbeddable> privileges;
        if (dbAccessRole.getPrivileges() != null) {
            privileges = dbAccessRole.getPrivileges();
        } else {
            privileges = new HashSet<>();
        }

        return AccessRoleDto.builder()
                .id(dbAccessRole.getId())
                .name(dbAccessRole.getName())
                .privileges(
                        privileges.stream()
                                .map(PrivilegeDto::from)
                                .collect(Collectors.toSet())
                )
                .build();
    }

}
