package org.myorg.module.core.access.context.source;

import lombok.Getter;
import org.myorg.module.auth.access.context.source.ApiKeySource;
import org.myorg.module.core.access.privilege.AccessOpCollection;
import org.myorg.module.core.access.privilege.PrivilegePair;

import java.util.Map;
import java.util.Set;

@Getter
public class CoreApiKeySource extends PrivilegeAuthorizing implements ApiKeySource {

    private long id;

    public CoreApiKeySource(long id, Map<String, AccessOpCollection> privileges) {
        super(privileges);
        this.id = id;
    }

    public CoreApiKeySource(long id, Set<PrivilegePair> privilegePairs) {
        super(privilegePairs);
        this.id = id;
    }
}
