package org.myorg.module.core.database.service.apikey;

import org.apache.commons.lang3.StringUtils;
import org.myorg.module.core.database.dao.AccessRoleDAO;
import org.myorg.module.core.database.dao.ApiKeyDAO;
import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.module.core.database.domainobject.DbApiKey;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.modules.access.context.Context;
import org.myorg.modules.crypto.CryptoService;
import org.myorg.modules.modules.database.service.DomainObjectService;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiKeyService extends DomainObjectService<DbApiKey, ApiKeyDAO, ApiKeyBuilder, ApiKeyDto> {

    private final AccessRoleDAO accessRoleDAO;
    private final CryptoService cryptoService;

    @Autowired
    public ApiKeyService(ApiKeyDAO apiKeyDAO, AccessRoleDAO accessRoleDAO, CryptoService cryptoService) {
        super(
                apiKeyDAO,
                dbApiKey -> {
                    if (dbApiKey == null) {
                        return null;
                    }
                    String value = cryptoService.decodeAsString(dbApiKey.getValue());
                    return ApiKeyDto.create(dbApiKey.getId(), dbApiKey.getName(), value);
                });
        this.accessRoleDAO = accessRoleDAO;
        this.cryptoService = cryptoService;
    }


    @Override
    public ApiKeyDto create(ApiKeyBuilder builder, Context<?> context) throws ModuleException {
        if (!builder.isContainName() || StringUtils.isEmpty(builder.getName())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbApiKey.class, DbApiKey.FIELD_NAME);
        }

        if (!builder.isContainValue() || StringUtils.isEmpty(builder.getValue())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbApiKey.class, DbApiKey.FIELD_VALUE);
        }

        DbApiKey dbApiKey = new DbApiKey();
        setFields(dbApiKey, builder);

        dbApiKey = dao.makePersistent(dbApiKey);
        return dtoBuilder.apply(dbApiKey);
    }

    @Override
    public ApiKeyDto update(long id, ApiKeyBuilder builder, Context<?> context) throws ModuleException {
        DbApiKey dbApiKey = dao.checkExistenceAndGet(id);

        if (builder.isContainName() && StringUtils.isEmpty(builder.getName())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbApiKey.class, DbApiKey.FIELD_NAME);
        }

        if (builder.isContainValue() && StringUtils.isEmpty(builder.getValue())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbApiKey.class, DbApiKey.FIELD_VALUE);
        }

        setFields(dbApiKey, builder);

        dbApiKey = dao.makePersistent(dbApiKey);
        return dtoBuilder.apply(dbApiKey);
    }

    @Transactional(readOnly = true)
    public Set<AccessRoleDto> findAllAccessRoles(long apiKeyId) throws ModuleException {
        DbApiKey dbApiKey = dao.checkExistenceAndGet(apiKeyId);
        return dbApiKey.getAccessRoles().stream()
                .map(AccessRoleDto::from)
                .collect(Collectors.toSet());
    }

    @Transactional
    public ApiKeyDto addAccessRole(long apiKeyId, long accessRoleId) throws ModuleException {
        DbApiKey dbApiKey = dao.checkExistenceAndGet(apiKeyId);
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndGet(accessRoleId);

        dbApiKey.addAccessRole(dbAccessRole);
        dbApiKey = dao.makePersistent(dbApiKey);
        return dtoBuilder.apply(dbApiKey);
    }

    @Transactional
    public ApiKeyDto removeAccessRole(long apiKeyId, long accessRoleId) throws ModuleException {
        DbApiKey dbApiKey = dao.checkExistenceAndGet(apiKeyId);
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndGet(accessRoleId);

        dbApiKey.getAccessRoles().remove(dbAccessRole);
        dbApiKey = dao.makePersistent(dbApiKey);
        return dtoBuilder.apply(dbApiKey);
    }

    @Transactional(readOnly = true)
    public ApiKeyDto findByValue(String value) throws ModuleException {
        for (ApiKeyDto apiKeyDto : findAll()) {
            if (Objects.equals(apiKeyDto.getValue(), value)) {
                return apiKeyDto;
            }
        }

        return null;
    }

    private void setFields(DbApiKey dbApiKey, ApiKeyBuilder builder) throws ModuleException {
        if (builder.isContainName()) {
            dao.checkUniqueness(
                    dbApiKey,
                    () -> dao.findByName(builder.getName()),
                    () -> ModuleExceptionBuilder.buildNotUniqueDomainObjectException(DbApiKey.class, DbApiKey.FIELD_NAME, builder.getName())
            );
            dbApiKey.setName(builder.getName());
        }

        if (builder.isContainValue()) {
            dbApiKey.setValue(cryptoService.encode(builder.getValue()));
        }
    }
}
