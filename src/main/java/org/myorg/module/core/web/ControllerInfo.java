package org.myorg.module.core.web;

import org.myorg.module.auth.access.context.Context;
import org.myorg.module.core.access.privilege.AbstractPrivilege;
import org.myorg.module.core.access.privilege.AccessOp;

public class ControllerInfo {

    private AbstractPrivilege privilege;
    private AccessOp[] accessOps;
    private Class<? extends Context> context;

    public ControllerInfo(AbstractPrivilege privilege,
                          AccessOp[] accessOps,
                          Class<? extends Context> context) {
        this.privilege = privilege;
        this.accessOps = accessOps;
        this.context = context;
    }

    public AbstractPrivilege getPrivilege() {
        return privilege;
    }

    public AccessOp[] getAccessOps() {
        return accessOps;
    }

    public Class<? extends Context> getContext() {
        return context;
    }
}
