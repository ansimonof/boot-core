package org.myorg.module.core.database.dao;

import org.myorg.module.core.database.domainobject.DbAbstractConfig;
import org.myorg.modules.modules.database.dao.GenericDAOImpl;

import java.util.HashMap;
import java.util.Optional;

public class ConfigDAO<T extends DbAbstractConfig> extends GenericDAOImpl<T> {

    public ConfigDAO(Class<T> clazz) {
        super(clazz);
    }

    public Optional<byte[]> getValue(String key) {
        Optional<T> config = execNamedQuery(DbAbstractConfig.QUERY_FIND_BY_KEY, new HashMap<String, Object>() {{
            put(DbAbstractConfig.FIELD_KEY, key);
        }}).findFirst();

        return config.isPresent() ? Optional.ofNullable(config.get().getValue()) : Optional.empty();
    }

    public T getDbConfig(String key) {
        return execNamedQuery(DbAbstractConfig.QUERY_FIND_BY_KEY, new HashMap<String, Object>() {{
            put(DbAbstractConfig.FIELD_KEY, key);
        }})
                .findFirst()
                .orElse(null);
    }
}
