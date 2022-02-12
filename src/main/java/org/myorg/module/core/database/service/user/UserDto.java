package org.myorg.module.core.database.service.user;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;
import org.myorg.module.core.database.domainobject.DbUser;
import org.myorg.modules.modules.dto.AbstractDto;

@JsonTypeName("user")
@JsonTypeInfo(include = JsonTypeInfo.As.WRAPPER_OBJECT, use = JsonTypeInfo.Id.NAME)
@Getter
@Setter
@EqualsAndHashCode(callSuper = false, of = { "id" })
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto implements AbstractDto {

    private long id;

    private String username;

    private String passwordHash;

    private boolean isEnabled;

    private boolean isAdmin;

    public static UserDto from(DbUser dbUser) {
        if (dbUser == null) {
            return null;
        }

        return UserDto.builder()
                .id(dbUser.getId())
                .username(dbUser.getUsername())
                .passwordHash(dbUser.getPasswordHash())
                .isAdmin(dbUser.isAdmin())
                .isEnabled(dbUser.isEnabled())
                .build();
    }

}
