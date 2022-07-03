package org.myorg.module.core.privilege;

import org.myorg.module.core.access.privilege.AbstractPrivilege;
import org.myorg.module.core.access.privilege.AccessOp;

public class UserManagementPrivilege extends AbstractPrivilege {

    public static final UserManagementPrivilege INSTANCE = new UserManagementPrivilege();

    private UserManagementPrivilege() {
        super(
                "core.module.user_management",
                AccessOp.READ, AccessOp.WRITE, AccessOp.DELETE
        );
    }

}
