package org.myorg.module.core.privilege;

import org.myorg.module.core.access.privilege.AbstractPrivilege;
import org.myorg.module.core.access.privilege.AccessOp;

public class ApiKeyRoleManagementPrivilege extends AbstractPrivilege {

    public static final ApiKeyRoleManagementPrivilege INSTANCE = new ApiKeyRoleManagementPrivilege();

    private ApiKeyRoleManagementPrivilege() {
        super(
                "core.module.api_key_management",
                AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE
        );
    }

    @Override
    public AbstractPrivilege getInstance() {
        return INSTANCE;
    }
}
