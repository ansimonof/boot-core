package org.myorg.module.core.database.service.apikey;

import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.database.service.DomainObjectService;
import org.myorg.modules.modules.exception.ModuleException;

import java.util.Set;

public interface ApiKeyService extends DomainObjectService<ApiKeyBuilder, ApiKeyDto> {

    Set<AccessRoleDto> findAllAccessRoles(long apiKeyId, Context<?> context) throws ModuleException;

    ApiKeyDto addAccessRole(long apiKeyId, long accessRoleId, Context<?> context) throws ModuleException;

    ApiKeyDto removeAccessRole(long apiKeyId, long accessRoleId, Context<?> context) throws ModuleException;

    ApiKeyDto findByValue(String value, Context<?> context) throws ModuleException;
}
