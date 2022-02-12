package org.myorg.module.core.access.privilege;

public abstract class AbstractPrivilege {

    private final String key;
    private final AccessOpCollection accessOpCollection;

    public AbstractPrivilege(String key, AccessOp... accessOps) {
        this.key = key;
        this.accessOpCollection = new AccessOpCollection(accessOps);
    }

    public String getKey() {
        return key;
    }

    public AccessOpCollection getAccessOpCollection() {
        return accessOpCollection;
    }

    public abstract AbstractPrivilege getInstance();

}
