package org.myorg.module.core.privilege;

import org.myorg.module.core.access.privilege.AbstractPrivilege;
import org.myorg.module.core.access.privilege.AccessOp;

public class AccessRoleManagementPrivilege extends AbstractPrivilege {

    public static final AccessRoleManagementPrivilege INSTANCE = new AccessRoleManagementPrivilege();

    private AccessRoleManagementPrivilege() {
        super(
                "core.accessrolemanagement",
                AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE
        );
    }

}
