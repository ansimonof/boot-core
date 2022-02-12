package org.myorg.module.core.access;

import org.myorg.module.core.access.privilege.AbstractPrivilege;
import org.myorg.module.core.access.privilege.getter.ModulePrivilegeGetter;
import org.myorg.module.core.privilege.AccessRoleManagementPrivilege;
import org.myorg.module.core.privilege.UserManagementPrivilege;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CorePrivilegeGetter extends ModulePrivilegeGetter {

    @Override
    public List<? extends AbstractPrivilege> getPrivileges() {
        return new ArrayList<AbstractPrivilege>() {{
            add(UserManagementPrivilege.INSTANCE);
            add(AccessRoleManagementPrivilege.INSTANCE);
        }};
    }
}
