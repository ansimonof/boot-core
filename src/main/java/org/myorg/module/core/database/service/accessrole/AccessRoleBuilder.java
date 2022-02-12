package org.myorg.module.core.database.service.accessrole;

import org.myorg.module.core.database.domainobject.PrivilegeEmbeddable;
import org.myorg.modules.utils.DomainObjectBuilder;

import java.util.Set;

public class AccessRoleBuilder extends DomainObjectBuilder {

    private final BuilderField<String> name = new BuilderField<>();
    private final BuilderField<Set<PrivilegeEmbeddable>> privileges = new BuilderField<>();

    public static AccessRoleBuilder builder() {
        return new AccessRoleBuilder();
    }

    public AccessRoleBuilder name(String name) {
        this.name.setValue(name);
        return this;
    }

    public AccessRoleBuilder privileges(Set<PrivilegeEmbeddable> privileges) {
        this.privileges.setValue(privileges);
        return this;
    }

    //---------------------

    public String getName() {
        return name.getValue();
    }

    public Set<PrivilegeEmbeddable> getPrivileges() {
        return privileges.getValue();
    }

    //-------------------

    public boolean isContainName() {
        return name.isContain();
    }

    public boolean isContainPrivileges() {
        return privileges.isContain();
    }
}
