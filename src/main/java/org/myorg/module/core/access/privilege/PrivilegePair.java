package org.myorg.module.core.access.privilege;

import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

public class PrivilegePair implements GrantedAuthority {

    private final String key;
    private final AccessOpCollection accessOpCollection;

    public PrivilegePair(String key, AccessOpCollection accessOpCollection) {
        this.key = key;
        this.accessOpCollection = accessOpCollection;
    }

    public PrivilegePair(String key, AccessOp... accessOps) {
        this(key, new AccessOpCollection(accessOps));
    }

    public String getKey() {
        return key;
    }

    public AccessOpCollection getAccessOpCollection() {
        return accessOpCollection;
    }

    @Override
    public String getAuthority() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PrivilegePair that = (PrivilegePair) o;
        return Objects.equals(key, that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }
}
