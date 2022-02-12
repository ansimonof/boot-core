package org.myorg.module.core.access.context.source;

import lombok.Getter;
import org.myorg.module.auth.access.context.source.UserSource;
import org.myorg.module.core.access.privilege.AccessOpCollection;
import org.myorg.module.core.access.privilege.PrivilegePair;

import java.util.Map;
import java.util.Set;

@Getter
public class CoreUserSource extends PrivilegeAuthorizing implements UserSource {

    private long id;

    public CoreUserSource(long id, Map<String, AccessOpCollection> privileges) {
        super(privileges);
        this.id = id;
    }

    public CoreUserSource(long id, Set<PrivilegePair> privileges) {
        super(privileges);
        this.id = id;
    }
}
