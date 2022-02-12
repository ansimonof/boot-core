package org.myorg.module.core.database.service.accessrole;

import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.modules.modules.database.service.DomainObjectService;
import org.myorg.modules.modules.exception.ModuleException;

import java.util.Set;

public interface AccessRoleService extends DomainObjectService<DbAccessRole, AccessRoleBuilder, AccessRoleDto> {

    Set<PrivilegeDto> findAllPrivileges(long accessId) throws ModuleException;

    AccessRoleDto addPrivileges(long accessRoleId, Set<PrivilegeBuilder> builders) throws ModuleException;
}
