package org.myorg.module.core.database.service.accessrole;

import org.myorg.module.core.access.privilege.AccessOp;
import org.myorg.modules.utils.DomainObjectBuilder;

public class PrivilegeBuilder extends DomainObjectBuilder {

    private final BuilderField<String> key = new BuilderField<>();
    private final BuilderField<AccessOp[]> ops = new BuilderField<>();

    public static PrivilegeBuilder builder() {
        return new PrivilegeBuilder();
    }

    public PrivilegeBuilder key(String key) {
        this.key.setValue(key);
        return this;
    }

    public PrivilegeBuilder ops(AccessOp... accessOps) {
        this.ops.setValue(accessOps);
        return this;
    }

    //--------------------------

    public String getKey() {
        return key.getValue();
    }

    public AccessOp[] getOps() {
        return ops.getValue();
    }

    //--------------------------

    public boolean isContainKey() {
        return key.isContain();
    }

    public boolean isContainOps() {
        return ops.isContain();
    }
}
