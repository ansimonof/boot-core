package org.myorg.module.core.database.dao;

import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.modules.modules.database.dao.GenericDAOImpl;
import org.springframework.stereotype.Service;

@Service
public class AccessRoleDAO extends GenericDAOImpl<DbAccessRole> {

    public AccessRoleDAO() {
        super(DbAccessRole.class);
    }

    public DbAccessRole findByName(String name) {
        return em.createNamedQuery(DbAccessRole.QUERY_FIND_BY_NAME, DbAccessRole.class)
                .setParameter(DbAccessRole.FIELD_NAME, name)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
    }

}
