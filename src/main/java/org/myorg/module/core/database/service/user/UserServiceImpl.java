package org.myorg.module.core.database.service.user;

import org.apache.commons.lang3.StringUtils;
import org.myorg.module.auth.access.context.AuthenticatedContext;
import org.myorg.module.core.exception.CoreExceptionBuilder;
import org.myorg.modules.access.context.Context;
import org.myorg.module.auth.exception.AuthExceptionBuilder;
import org.myorg.module.core.access.context.source.CoreUserSource;
import org.myorg.module.core.database.dao.AccessRoleDAO;
import org.myorg.module.core.database.dao.UserDAO;
import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.module.core.database.domainobject.DbUser;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class UserServiceImpl implements UserService {

    private final UserDAO userDAO;
    private final AccessRoleDAO accessRoleDAO;

    @Autowired
    public UserServiceImpl(UserDAO userDAO, AccessRoleDAO accessRoleDAO) {
        this.userDAO = userDAO;
        this.accessRoleDAO = accessRoleDAO;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(long id, Context<?> context) throws ModuleException {
        return UserDto.from(userDAO.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UserDto> findAll(Context<?> context) throws ModuleException {
        return userDAO.findAll().stream()
                .map(UserDto::from)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
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

        if (!builder.isContainAdmin() || builder.getIsAdmin() == null) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_IS_ADMIN);
        }

        DbUser dbUser = new DbUser();
        setFields(dbUser, builder);

        return UserDto.from(userDAO.makePersistent(dbUser));
    }

    @Override
    @Transactional
    public UserDto update(long id, UserBuilder builder, Context<?> context) throws ModuleException {
        DbUser dbUser = userDAO.checkExistenceAndReturn(id);

        if (builder.isContainUsername() && StringUtils.isEmpty(builder.getUsername())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_USERNAME);
        }

        if (builder.isContainPasswordHash() && StringUtils.isEmpty(builder.getPasswordHash())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_PASSWORD_HASH);
        }

        if (builder.isContainEnabled() && builder.getIsEnabled() == null) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_IS_ENABLED);
        }

        if (builder.isContainAdmin() && builder.getIsAdmin() == null) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbUser.class, DbUser.FIELD_IS_ADMIN);
        }

        setFields(dbUser, builder);

        return UserDto.from(userDAO.makePersistent(dbUser));
    }

    @Override
    @Transactional
    public void remove(long id, Context<?> context) throws ModuleException {
        DbUser dbUser = userDAO.findById(id);
        if (dbUser != null) {
            userDAO.makeTransient(dbUser);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUsername(String username, Context<?> context) throws ModuleException {
        return UserDto.from(userDAO.findByUsername(username));
    }

    @Override
    @Transactional(readOnly = true)
    public Set<UserDto> findAdmins(Context<?> context) throws ModuleException {
        return userDAO.execNamedQuery(DbUser.QUERY_FIND_ADMINS, Collections.emptyMap())
                .map(UserDto::from)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public UserDto banUser(long userId, Context<?> context) throws ModuleException {
        if (!(context instanceof AuthenticatedContext)) {
            throw AuthExceptionBuilder.buildInvalidAuthenticationContextException(context.getClass(), AuthenticatedContext.class);
        }

        if (!(context.getSource() instanceof CoreUserSource)) {
            throw AuthExceptionBuilder.buildInvalidRequestSourceException(context.getSource().getClass(), CoreUserSource.class);
        }

        CoreUserSource userSource = (CoreUserSource) context.getSource();
        DbUser dbUser = userDAO.findById(userSource.getId());
        DbUser userForBan = userDAO.findById(userId);
        if (!dbUser.isAdmin() && userForBan.isAdmin()) {
            throw CoreExceptionBuilder.buildAdminCannotBeBannedException();
        }

        return update(userId, UserBuilder.builder().isEnabled(false), context);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<AccessRoleDto> findAllAccessRoles(long userId, Context<?> context) throws ModuleException {
        DbUser dbUser = userDAO.checkExistenceAndReturn(userId);
        return dbUser.getAccessRoles().stream()
                .map(AccessRoleDto::from)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public UserDto addAccessRole(long userId, long accessRoleId, Context<?> context) throws ModuleException {
        DbUser dbUser = userDAO.checkExistenceAndReturn(userId);
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndReturn(accessRoleId);
        dbUser.addAccessRole(dbAccessRole);

        return UserDto.from(userDAO.makePersistent(dbUser));
    }

    @Override
    @Transactional
    public UserDto removeAccessRole(long userId, long accessRoleId, Context<?> context) throws ModuleException {
        DbUser dbUser = userDAO.checkExistenceAndReturn(userId);
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndReturn(accessRoleId);
        dbUser.getAccessRoles().remove(dbAccessRole);

        return UserDto.from(userDAO.makePersistent(dbUser));
    }

    private void setFields(DbUser dbUser,
                           UserBuilder builder) throws ModuleException {
        if (builder.isContainUsername()) {
            userDAO.checkUniqueness(
                    dbUser,
                    () -> userDAO.findByUsername(builder.getUsername()),
                    () -> ModuleExceptionBuilder.buildNotUniqueDomainObjectException(DbUser.class, DbUser.FIELD_USERNAME, builder.getUsername()));
            dbUser.setUsername(builder.getUsername());
        }

        if (builder.isContainPasswordHash()) {
            dbUser.setPasswordHash(builder.getPasswordHash());
        }

        if (builder.isContainEnabled()) {
            dbUser.setEnabled(builder.getIsEnabled());
        }

        if (builder.isContainAdmin()) {
            dbUser.setAdmin(builder.getIsAdmin());
        }

    }

}
