package org.myorg.module.core.database.service.apikey;

import org.myorg.module.core.database.domainobject.DbApiKey;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.modules.modules.database.service.DomainObjectService;
import org.myorg.modules.modules.exception.ModuleException;

import java.util.Set;

public interface ApiKeyService extends DomainObjectService<DbApiKey, ApiKeyBuilder, ApiKeyDto> {

    Set<AccessRoleDto> findAllAccessRoles(long apiKeyId) throws ModuleException;

    ApiKeyDto addAccessRole(long apiKeyId, long accessRoleId) throws ModuleException;

    ApiKeyDto removeAccessRole(long apiKeyId, long accessRoleId) throws ModuleException;

    ApiKeyDto findByValue(String value) throws ModuleException;
}
