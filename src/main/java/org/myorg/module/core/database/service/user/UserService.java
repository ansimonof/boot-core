package org.myorg.module.core.database.service.user;

import org.myorg.modules.access.context.Context;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.modules.modules.database.service.DomainObjectService;
import org.myorg.modules.modules.exception.ModuleException;

import java.util.Set;

public interface UserService extends DomainObjectService<UserBuilder, UserDto> {

    UserDto findByUsername(String username, Context<?> context) throws ModuleException;

    UserDto banUser(long userId, Context<?> context) throws ModuleException;

    Set<AccessRoleDto> findAllAccessRoles(long userId, Context<?> context) throws ModuleException;

    UserDto addAccessRole(long userId, long accessRoleId, Context<?> context) throws ModuleException;

    UserDto removeAccessRole(long userId, long accessRoleId, Context<?> context) throws ModuleException;
}
