package org.myorg.module.core.database.service.config;

import org.myorg.module.core.config.CoreConfigDescriptions;
import org.myorg.module.core.config.ServerStatus;
import org.myorg.module.core.database.dao.CoreConfigDAO;
import org.myorg.module.core.database.domainobject.DbCoreConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CoreConfigService extends ConfigService<DbCoreConfig, CoreConfigDAO> {

    @Autowired
    public CoreConfigService(CoreConfigDAO dao) {
        super(DbCoreConfig.class, dao);
    }

    @Override
    public void createConfigIfNotExist() {
        Optional<ServerStatus> serverStatus = get(CoreConfigDescriptions.Server.STATUS);
        if (!serverStatus.isPresent()) {
            setServerStatus(CoreConfigDescriptions.Server.STATUS.getDefaultValue());
        }
    }

    public ServerStatus getServerStatus() {
        return get(CoreConfigDescriptions.Server.STATUS).orElse(null);
    }

    public void setServerStatus(ServerStatus serverStatus) {
        set(CoreConfigDescriptions.Server.STATUS, serverStatus);
    }

}
