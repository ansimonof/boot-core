package org.myorg.module.core.access.privilege.getter;


import org.myorg.module.core.access.privilege.AbstractPrivilege;

import java.util.List;

public abstract class ModulePrivilegeGetter {

    public abstract List<? extends AbstractPrivilege> getPrivileges();
}
