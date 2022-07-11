package org.myorg.module.core.database.service.accessrole;

import org.myorg.modules.utils.DomainObjectBuilder;

public class AccessRoleBuilder extends DomainObjectBuilder {

    private final BuilderField<String> name = new BuilderField<>();

    public static AccessRoleBuilder builder() {
        return new AccessRoleBuilder();
    }

    public AccessRoleBuilder name(String name) {
        this.name.setValue(name);
        return this;
    }

    //---------------------

    public String getName() {
        return name.getValue();
    }

    //-------------------

    public boolean isContainName() {
        return name.isContain();
    }

}
