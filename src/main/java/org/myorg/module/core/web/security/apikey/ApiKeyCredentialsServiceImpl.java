package org.myorg.module.core.web.security.apikey;

import org.myorg.module.auth.access.context.source.ApiKeySource;
import org.myorg.module.auth.service.apikey.ApiKeyCredentialsService;
import org.myorg.module.core.access.context.source.CoreApiKeySource;
import org.myorg.module.core.access.privilege.PrivilegePair;
import org.myorg.module.core.access.privilege.getter.PrivilegeGetter;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.apikey.ApiKeyDto;
import org.myorg.module.core.database.service.apikey.ApiKeyService;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class ApiKeyCredentialsServiceImpl implements ApiKeyCredentialsService {

    private final ApiKeyService apiKeyService;
    private final PrivilegeGetter privilegeGetter;

    @Autowired
    public ApiKeyCredentialsServiceImpl(ApiKeyService apiKeyService, PrivilegeGetter privilegeGetter) {
        this.apiKeyService = apiKeyService;
        this.privilegeGetter = privilegeGetter;
    }

    @Override
    public boolean isApiKeyExists(String apiKey) throws ModuleException {
        return apiKeyService.findByValue(apiKey, ContextUtils.createSystemContext()) != null;
    }

    @Override
    public ApiKeySource createSource(String apiKey) throws ModuleException {
        ApiKeyDto apiKeyDto = apiKeyService.findByValue(apiKey, ContextUtils.createSystemContext());
        return new CoreApiKeySource(apiKeyDto.getId(), getPrivileges(apiKeyDto));
    }

    private Set<PrivilegePair> getPrivileges(ApiKeyDto apiKeyDto) throws ModuleException {
        Set<AccessRoleDto> accessRoleDtos = apiKeyService.findAllAccessRoles(apiKeyDto.getId(), ContextUtils.createSystemContext());
        return privilegeGetter.mergeAccessRoles(accessRoleDtos);
    }
}
