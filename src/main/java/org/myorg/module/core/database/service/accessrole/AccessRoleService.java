package org.myorg.module.core.database.service.accessrole;

import org.apache.commons.lang3.StringUtils;
import org.myorg.module.core.database.dao.AccessRoleDAO;
import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.module.core.database.domainobject.PrivilegeEmbeddable;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.database.service.DomainObjectService;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class AccessRoleService
        extends DomainObjectService<DbAccessRole, AccessRoleDAO, AccessRoleBuilder, AccessRoleDto> {

    @Autowired
    public AccessRoleService(AccessRoleDAO accessRoleDAO) {
        super(accessRoleDAO, AccessRoleDto::from);
    }

    @Override
    public AccessRoleDto create(AccessRoleBuilder builder, Context<?> context) throws ModuleException {
        if (!builder.isContainName() || StringUtils.isEmpty(builder.getName())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbAccessRole.class, DbAccessRole.FIELD_NAME);
        }

        DbAccessRole dbAccessRole = new DbAccessRole();
        setFields(dbAccessRole, builder);

        return dtoBuilder.apply(dao.makePersistent(dbAccessRole));
    }

    @Override
    public AccessRoleDto update(long id, AccessRoleBuilder builder, Context<?> context) throws ModuleException {
        DbAccessRole dbAccessRole = dao.checkExistenceAndGet(id);

        if (builder.isContainName() && StringUtils.isEmpty(builder.getName())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbAccessRole.class, DbAccessRole.FIELD_NAME);
        }

        setFields(dbAccessRole, builder);

        return dtoBuilder.apply(dao.makePersistent(dbAccessRole));
    }


    @Transactional(readOnly = true)
    public Set<PrivilegeDto> findAllPrivileges(long accessId) throws ModuleException {
        DbAccessRole dbAccessRole = dao.checkExistenceAndGet(accessId);
        AccessRoleDto accessRoleDto = dtoBuilder.apply(dbAccessRole);
        return accessRoleDto.getPrivileges();
    }

    @Transactional
    public AccessRoleDto addPrivileges(long accessRoleId, Set<PrivilegeBuilder> builders) throws ModuleException {
        DbAccessRole dbAccessRole = dao.checkExistenceAndGet(accessRoleId);

        Set<PrivilegeEmbeddable> privileges = new HashSet<>();
        for (PrivilegeBuilder builder : builders) {
            if (!builder.isContainKey() || StringUtils.isEmpty(builder.getKey())) {
                throw ModuleExceptionBuilder.buildEmptyValueException(PrivilegeEmbeddable.FIELD_KEY);
            }

            if (!builder.isContainOps() || builder.getOps() == null) {
                throw ModuleExceptionBuilder.buildEmptyValueException(PrivilegeEmbeddable.FIELD_VALUE);
            }

            PrivilegeEmbeddable privilege = new PrivilegeEmbeddable();
            privilege.setKey(builder.getKey());
            privilege.setValue(builder.getOps());
            privileges.add(privilege);
        }

        dbAccessRole.setPrivileges(privileges);
        return dtoBuilder.apply(dao.makePersistent(dbAccessRole));
    }

    private void setFields(DbAccessRole dbAccessRole, AccessRoleBuilder builder) throws ModuleException {
        if (builder.isContainName()) {
            dao.checkUniqueness(
                    dbAccessRole,
                    () -> dao.findByName(builder.getName()),
                    () -> ModuleExceptionBuilder.buildNotUniqueDomainObjectException(DbAccessRole.class, DbAccessRole.FIELD_NAME, builder.getName())
            );
            dbAccessRole.setName(builder.getName());
        }
    }
}
