package org.myorg.module.core.database.service.accessrole;

import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.database.service.DomainObjectService;
import org.myorg.modules.modules.exception.ModuleException;

import java.util.Set;

public interface AccessRoleService extends DomainObjectService<AccessRoleBuilder, AccessRoleDto> {

    Set<PrivilegeDto> findAllPrivileges(long accessId, Context<?> context) throws ModuleException;

    AccessRoleDto addPrivileges(long accessRoleId, Set<PrivilegeBuilder> builders, Context<?> context) throws ModuleException;
}
