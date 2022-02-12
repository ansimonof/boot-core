package org.myorg.module.core.access.context.source;

import org.myorg.module.core.access.privilege.AccessOpCollection;
import org.myorg.module.core.access.privilege.PrivilegePair;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class PrivilegeAuthorizing {

    protected Map<String, AccessOpCollection> privileges;

    public PrivilegeAuthorizing(Map<String, AccessOpCollection> privileges) {
        this.privileges = privileges;
    }

    public PrivilegeAuthorizing(Set<PrivilegePair> privilegePairs) {
        this.privileges = privilegePairs.stream().collect(
                Collectors.toMap(PrivilegePair::getKey, PrivilegePair::getAccessOpCollection));
    }

    public Map<String, AccessOpCollection> getPrivileges() {
        return privileges;
    }
}
