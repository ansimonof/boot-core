package org.myorg.module.core.database.service.apikey;

import org.apache.commons.lang3.StringUtils;
import org.myorg.module.core.database.dao.AccessRoleDAO;
import org.myorg.module.core.database.dao.ApiKeyDAO;
import org.myorg.module.core.database.domainobject.DbAccessRole;
import org.myorg.module.core.database.domainobject.DbApiKey;
import org.myorg.module.core.database.service.accessrole.AccessRoleDto;
import org.myorg.modules.crypto.CryptoService;
import org.myorg.modules.modules.exception.ModuleException;
import org.myorg.modules.modules.exception.ModuleExceptionBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ApiKeyServiceImpl implements ApiKeyService {

    private final ApiKeyDAO apiKeyDAO;
    private final AccessRoleDAO accessRoleDAO;
    private final CryptoService cryptoService;

    @Autowired
    public ApiKeyServiceImpl(ApiKeyDAO apiKeyDAO, AccessRoleDAO accessRoleDAO, CryptoService cryptoService) {
        this.apiKeyDAO = apiKeyDAO;
        this.accessRoleDAO = accessRoleDAO;
        this.cryptoService = cryptoService;
    }

    @Override
    @Transactional(readOnly = true)
    public ApiKeyDto findById(long id) throws ModuleException {
        DbApiKey dbApiKey = apiKeyDAO.findById(id);
        if (dbApiKey == null) {
            return null;
        }

        return createDto(dbApiKey);
    }

    @Override
    @Transactional(readOnly = true)
    public Set<ApiKeyDto> findAll() throws ModuleException {
        return apiKeyDAO.findAll().stream()
                .map(this::createDto)
                .collect(Collectors.toSet());
    }


    @Override
    @Transactional
    public ApiKeyDto create(ApiKeyBuilder builder) throws ModuleException {
        if (!builder.isContainName() || StringUtils.isEmpty(builder.getName())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbApiKey.class, DbApiKey.FIELD_NAME);
        }

        if (!builder.isContainValue() || StringUtils.isEmpty(builder.getValue())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbApiKey.class, DbApiKey.FIELD_VALUE);
        }

        DbApiKey dbApiKey = new DbApiKey();
        setFields(dbApiKey, builder);

        dbApiKey = apiKeyDAO.makePersistent(dbApiKey);
        return createDto(dbApiKey);
    }

    @Override
    @Transactional
    public ApiKeyDto update(long id, ApiKeyBuilder builder) throws ModuleException {
        DbApiKey dbApiKey = apiKeyDAO.checkExistenceAndReturn(id);

        if (builder.isContainName() && StringUtils.isEmpty(builder.getName())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbApiKey.class, DbApiKey.FIELD_NAME);
        }

        if (builder.isContainValue() && StringUtils.isEmpty(builder.getValue())) {
            throw ModuleExceptionBuilder.buildEmptyValueException(DbApiKey.class, DbApiKey.FIELD_VALUE);
        }

        setFields(dbApiKey, builder);

        dbApiKey = apiKeyDAO.makePersistent(dbApiKey);
        return createDto(dbApiKey);
    }

    @Override
    @Transactional
    public void remove(long id) throws ModuleException {
        DbApiKey dbApiKey = apiKeyDAO.findById(id);
        if (dbApiKey != null) {
            apiKeyDAO.makeTransient(dbApiKey);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Set<AccessRoleDto> findAllAccessRoles(long apiKeyId) throws ModuleException {
        DbApiKey dbApiKey = apiKeyDAO.checkExistenceAndReturn(apiKeyId);
        return dbApiKey.getAccessRoles().stream()
                .map(AccessRoleDto::from)
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional
    public ApiKeyDto addAccessRole(long apiKeyId, long accessRoleId) throws ModuleException {
        DbApiKey dbApiKey = apiKeyDAO.checkExistenceAndReturn(apiKeyId);
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndReturn(accessRoleId);

        dbApiKey.addAccessRole(dbAccessRole);
        dbApiKey = apiKeyDAO.makePersistent(dbApiKey);
        return createDto(dbApiKey);
    }

    @Override
    @Transactional
    public ApiKeyDto removeAccessRole(long apiKeyId, long accessRoleId) throws ModuleException {
        DbApiKey dbApiKey = apiKeyDAO.checkExistenceAndReturn(apiKeyId);
        DbAccessRole dbAccessRole = accessRoleDAO.checkExistenceAndReturn(accessRoleId);

        dbApiKey.getAccessRoles().remove(dbAccessRole);
        dbApiKey = apiKeyDAO.makePersistent(dbApiKey);
        return createDto(dbApiKey);
    }

    @Override
    @Transactional(readOnly = true)
    public ApiKeyDto findByValue(String value) throws ModuleException {
        for (ApiKeyDto apiKeyDto : findAll()) {
            if (Objects.equals(apiKeyDto.getValue(), value)) {
                return apiKeyDto;
            }
        }

        return null;
    }

    private ApiKeyDto createDto(DbApiKey dbApiKey) {
        String value = cryptoService.decodeAsString(dbApiKey.getValue());
        return ApiKeyDto.create(dbApiKey.getId(), dbApiKey.getName(), value);
    }

    private void setFields(DbApiKey dbApiKey, ApiKeyBuilder builder) throws ModuleException {
        if (builder.isContainName()) {
            apiKeyDAO.checkUniqueness(
                    dbApiKey,
                    () -> apiKeyDAO.findByName(builder.getName()),
                    () -> ModuleExceptionBuilder.buildNotUniqueDomainObjectException(DbApiKey.class, DbApiKey.FIELD_NAME, builder.getName())
            );
            dbApiKey.setName(builder.getName());
        }

        if (builder.isContainValue()) {
            dbApiKey.setValue(cryptoService.encode(builder.getValue()));
        }
    }
}
