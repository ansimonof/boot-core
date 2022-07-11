package org.myorg.module.core.database.dao;

import org.myorg.module.core.database.domainobject.DbCoreConfig;
import org.springframework.stereotype.Service;

@Service
public class CoreConfigDAO extends ConfigDAO<DbCoreConfig> {

    public CoreConfigDAO() {
        super(DbCoreConfig.class);
    }
}
