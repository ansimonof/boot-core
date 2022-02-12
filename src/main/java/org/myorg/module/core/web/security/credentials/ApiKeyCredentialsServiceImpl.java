package org.myorg.module.core.web.security.credentials;

import org.myorg.module.auth.access.context.source.ApiKeySource;
import org.myorg.module.auth.service.ApiKeyCredentialsService;
import org.myorg.module.auth.service.credentials.ApiKeyCredentials;
import org.myorg.module.core.access.context.source.CoreApiKeySource;
import org.myorg.module.core.access.privilege.PrivilegePair;
import org.myorg.module.core.access.privilege.getter.PrivilegeGetter;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.apikey.ApiKeyDto;
import org.myorg.module.core.database.service.apikey.ApiKeyService;
import org.myorg.modules.modules.exception.ModuleException;
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
    public boolean isCorrect(ApiKeyCredentials credentials) throws ModuleException {
        ApiKeyDto apiKeyDto = apiKeyService.findByValue(credentials.getApiKey());
        return apiKeyDto != null;
    }

    @Override
    public ApiKeySource createSource(ApiKeyCredentials credentials) throws ModuleException {
        ApiKeyDto apiKeyDto = apiKeyService.findByValue(credentials.getApiKey());
        return new CoreApiKeySource(apiKeyDto.getId(), getPrivileges(apiKeyDto));
    }

    private Set<PrivilegePair> getPrivileges(ApiKeyDto apiKeyDto) throws ModuleException {
        Set<AccessRoleDto> accessRoleDtos = apiKeyService.findAllAccessRoles(apiKeyDto.getId());
        return privilegeGetter.mergeAccessRoles(accessRoleDtos);
    }
}
