package org.myorg.module.core;

import org.myorg.module.auth.AuthModule;
import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.module.core.database.service.accessrole.AccessRoleBuilder;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.database.service.accessrole.AccessRoleService;
import org.myorg.module.core.database.service.accessrole.PrivilegeBuilder;
import org.myorg.module.core.database.service.apikey.ApiKeyBuilder;
import org.myorg.module.core.database.service.apikey.ApiKeyDto;
import org.myorg.module.core.database.service.apikey.ApiKeyService;
import org.myorg.module.core.privilege.AccessRoleManagementPrivilege;
import org.myorg.module.core.privilege.UserManagementPrivilege;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.BootModule;
import org.myorg.modules.modules.Module;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.utils.ContextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
@BootModule(
        uuid = CoreModuleConsts.UUID,
        dependencies = { AuthModule.class }
)
@PropertySource(
        value = "boot-properties/core.application.properties"
)
public class CoreModule extends Module {

    private final ApiKeyService apiKeyService;
    private final AccessRoleService accessRoleService;

    @Autowired
    public CoreModule(ApiKeyService apiKeyService, AccessRoleService accessRoleService) {
        this.apiKeyService = apiKeyService;
        this.accessRoleService = accessRoleService;
    }

    @Override
    public void init() throws ModuleException {
        PrivilegeBuilder privilege1 = PrivilegeBuilder.builder()
                .key(UserManagementPrivilege.INSTANCE.getKey())
                .ops(AccessOp.READ, AccessOp.DELETE);

        PrivilegeBuilder privilege2 = PrivilegeBuilder.builder()
                .key(AccessRoleManagementPrivilege.INSTANCE.getKey())
                .ops(AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE);


        Context<?> context = ContextUtils.createSystemContext();

        AccessRoleDto accessRoleDto = accessRoleService.create(AccessRoleBuilder.builder().name("ar"), context);
        accessRoleDto = accessRoleService.addPrivileges(accessRoleDto.getId(), new HashSet<PrivilegeBuilder>() {{
            add(privilege1);
            add(privilege2);
        }}, context);

        ApiKeyDto apiKeyDto = apiKeyService.create(ApiKeyBuilder.builder().name("APIQWE").value("123"), context);
        apiKeyService.addAccessRole(apiKeyDto.getId(), accessRoleDto.getId(), context);

    }

    @Override
    public void destroy() throws ModuleException {

    }
}
