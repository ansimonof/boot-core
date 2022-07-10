package org.myorg.module.core.database.service.user;

import org.apache.commons.lang3.StringUtils;
import org.myorg.module.core.database.dao.AccessRoleDAO;
import org.myorg.module.core.database.dao.UserDAO;
import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.module.core.database.domainobject.DbUser;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.module.core.exception.CoreExceptionBuilder;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.modules.database.service.DomainObjectService;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.ZoneId;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService extends DomainObjectService<DbUser, UserDAO, UserBuilder, UserDto> {

    private final AccessRoleDAO accessRoleDAO;

    @Autowired
    public UserService(UserDAO userDAO, AccessRoleDAO accessRoleDAO) {
        super(userDAO, UserDto::from);
        this.accessRoleDAO = accessRoleDAO;
    }

    @Override
    public UserDto create(UserBuilder builder, Context<?> context) throws ModuleException {
        if (!builder.isContainUsername() || StringUtils.isEmpty(builder.getUsername())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_USERNAME);
        }

        if (!builder.isContainPasswordHash() || StringUtils.isEmpty(builder.getPasswordHash())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_PASSWORD_HASH);
        }

        if (!builder.isContainEnabled() || builder.getIsEnabled() == null) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_IS_ENABLED);
        }

        if (!builder.isContainTimeZone() || StringUtils.isEmpty(builder.getTimeZone())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_TIME_ZONE);
        }

        DbUser dbUser = new DbUser();
        setFields(dbUser, builder, context);

        return UserDto.from(dao.makePersistent(dbUser));
    }

    @Override
    public UserDto update(long id, UserBuilder builder, Context<?> context) throws ModuleException {
        DbUser dbUser = dao.checkExistenceAndGet(id);

        if (builder.isContainUsername() && StringUtils.isEmpty(builder.getUsername())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_USERNAME);
        }

        if (builder.isContainPasswordHash() && StringUtils.isEmpty(builder.getPasswordHash())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_PASSWORD_HASH);
        }

        if (builder.isContainEnabled() && builder.getIsEnabled() == null) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_IS_ENABLED);
        }

        if (builder.isContainTimeZone() && StringUtils.isEmpty(builder.getTimeZone())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_TIME_ZONE);
        }

        setFields(dbUser, builder, context);

        return dtoBuilder.apply(dao.makePersistent(dbUser));
    }

    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) throws ModuleException {
        return dtoBuilder.apply(dao.findByUsername(username));
    }

    @Transactional(readOnly = true)
    public UserDto findAdmin() throws ModuleException {
        return dao.execNamedQuery(DbUser.QUERY_FIND_ADMIN, Collections.emptyMap())
                .map(UserDto::from)
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public Set<AccessRoleDto> findAllAccessRoles(long userId, Context<?> context) throws ModuleException {
        DbUser dbUser = dao.checkExistenceAndGet(userId);
        return dbUser.getAccessRoles().stream()
                .map(AccessRoleDto::from)
                .collect(Collectors.toSet());
    }

    @Transactional
    public UserDto addAccessRole(long userId, long accessRoleId, Context<?> context) throws ModuleException {
        DbUser dbUser = dao.checkExistenceAndGet(userId);
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndGet(accessRoleId);
        dbUser.addAccessRole(dbAccessRole);

        return dtoBuilder.apply(dao.makePersistent(dbUser));
    }

    @Transactional
    public UserDto removeAccessRole(long userId, long accessRoleId, Context<?> context) throws ModuleException {
        DbUser dbUser = dao.checkExistenceAndGet(userId);
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndGet(accessRoleId);
        dbUser.getAccessRoles().remove(dbAccessRole);

        return dtoBuilder.apply(dao.makePersistent(dbUser));
    }

    private void setFields(DbUser dbUser,
                           UserBuilder builder,
                           Context<?> context) throws ModuleException {
        if (builder.isContainUsername()) {
            dao.checkUniqueness(
                    dbUser,
                    () -> dao.findByUsername(builder.getUsername()),
                    () -> ModuleExceptionBuilder.buildNotUniqueDomainObjectException(DbUser.class, DbUser.FIELD_USERNAME, builder.getUsername()));
            dbUser.setUsername(builder.getUsername());
        }

        if (builder.isContainPasswordHash()) {
            dbUser.setPasswordHash(builder.getPasswordHash());
        }

        if (builder.isContainEnabled()) {
            if (dbUser.isAdmin() && !builder.getIsEnabled()) {
                throw CoreExceptionBuilder.buildAdminCannotBeBannedException();
            }
            dbUser.setEnabled(builder.getIsEnabled());
        }

        if (builder.isContainTimeZone()) {
            try {
                ZoneId.of(builder.getTimeZone());
            } catch (DateTimeException e) {
                throw ModuleExceptionBuilder.buildInternalServerErrorException(e);
            }
            dbUser.setTimeZone(builder.getTimeZone());
        }

        if (findAdmin() == null) {
            dbUser.setAdmin(true);
        } else {
            dbUser.setAdmin(false);
        }

    }

}
