package org.myorg.module.core.database.dao;

import org.myorg.module.core.database.domainobject.DbUser;
import org.myorg.modules.modules.database.dao.GenericDAOImpl;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class UserDAO extends GenericDAOImpl<DbUser> {

    public UserDAO() {
        super(DbUser.class);
    }

    public DbUser findByUsername(String username) {
        return execNamedQuery(DbUser.QUERY_FIND_BY_USERNAME, new HashMap<String, Object>() {{
            put(DbUser.FIELD_USERNAME, username);
        }}).findFirst().orElse(null);
    }

}
