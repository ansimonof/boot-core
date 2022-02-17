package org.myorg.module.core.database.service.accessrole;

import org.apache.commons.lang3.StringUtils;
import org.myorg.module.core.database.dao.AccessRoleDAO;
import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.module.core.database.domainobject.PrivilegeEmbeddable;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AccessRoleServiceImpl implements AccessRoleService {

    private final AccessRoleDAO accessRoleDAO;

    @Autowired
    public AccessRoleServiceImpl(AccessRoleDAO accessRoleDAO) {
        this.accessRoleDAO = accessRoleDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public AccessRoleDto findById(long id, Context<?> context) throws ModuleException {
        return AccessRoleDto.from(accessRoleDAO.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<AccessRoleDto> findAll(Context<?> context) throws ModuleException {
        return accessRoleDAO.findAll().stream()
                .map(AccessRoleDto::from)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public AccessRoleDto create(AccessRoleBuilder builder, Context<?> context) throws ModuleException {
        if (!builder.isContainName() || StringUtils.isEmpty(builder.getName())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbAccessRole.class, DbAccessRole.FIELD_NAME);
        }

        DbAccessRole dbAccessRole = new DbAccessRole();
        setFields(dbAccessRole, builder);

        return AccessRoleDto.from(accessRoleDAO.makePersistent(dbAccessRole));
    }

    @Override
    @Transactional
    public AccessRoleDto update(long id, AccessRoleBuilder builder, Context<?> context) throws ModuleException {
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndReturn(id);

        if (builder.isContainName() && StringUtils.isEmpty(builder.getName())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbAccessRole.class, DbAccessRole.FIELD_NAME);
        }

        setFields(dbAccessRole, builder);

        return AccessRoleDto.from(accessRoleDAO.makePersistent(dbAccessRole));
    }

    @Override
    @Transactional
    public void remove(long id, Context<?> context) throws ModuleException {
        DbAccessRole dbAccessRole = accessRoleDAO.findById(id);
        if (dbAccessRole != null) {
            accessRoleDAO.makeTransient(dbAccessRole);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<PrivilegeDto> findAllPrivileges(long accessId, Context<?> context) throws ModuleException {
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndReturn(accessId);
        AccessRoleDto accessRoleDto = AccessRoleDto.from(dbAccessRole);
        return accessRoleDto.getPrivileges();
    }

    @Override
    @Transactional
    public AccessRoleDto addPrivileges(long accessRoleId, Set<PrivilegeBuilder> builders, Context<?> context) throws ModuleException {
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndReturn(accessRoleId);

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
        return AccessRoleDto.from(accessRoleDAO.makePersistent(dbAccessRole));
    }

    private void setFields(DbAccessRole dbAccessRole, AccessRoleBuilder builder) throws ModuleException {
        if (builder.isContainName()) {
            accessRoleDAO.checkUniqueness(
                    dbAccessRole,
                    () -> accessRoleDAO.findByName(builder.getName()),
                    () -> ModuleExceptionBuilder.buildNotUniqueDomainObjectException(DbAccessRole.class, DbAccessRole.FIELD_NAME, builder.getName())
            );
            dbAccessRole.setName(builder.getName());
        }
    }
}
