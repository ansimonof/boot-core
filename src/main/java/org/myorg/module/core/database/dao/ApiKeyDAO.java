package org.myorg.module.core.database.dao;

import org.myorg.module.core.database.domainobject.DbApiKey;
import org.myorg.modules.modules.database.dao.GenericDAOImpl;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;

@Service
public class ApiKeyDAO extends GenericDAOImpl<DbApiKey> {

    public ApiKeyDAO() {
        super(DbApiKey.class);
    }

    public DbApiKey findByValue(byte[] value) {
        for (DbApiKey dbApiKey : findAll()) {
            if (Arrays.equals(value, dbApiKey.getValue())) {
                return dbApiKey;
            }
        }

        return null;
    }

    public DbApiKey findByName(String name) {
        return execNamedQuery(DbApiKey.QUERY_FIND_BY_NAME, new HashMap<String, Object>() {{
            put(DbApiKey.FIELD_NAME, name);
        }}).findFirst().orElse(null);
    }

}
