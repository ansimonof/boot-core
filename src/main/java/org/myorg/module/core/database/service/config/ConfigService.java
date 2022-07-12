package org.myorg.module.core.database.service.config;

import org.myorg.module.core.utils.config.Config;
import org.myorg.module.core.utils.config.TypeConverter;
import org.myorg.module.core.database.dao.ConfigDAO;
import org.myorg.module.core.database.domainobject.DbAbstractConfig;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public abstract class ConfigService<T extends DbAbstractConfig, U extends ConfigDAO<T>> {

    private final U dao;
    private final Class<T> clazz;

    public ConfigService(Class<T> clazz, U dao) {
        this.clazz = clazz;
        this.dao = dao;
    }

    protected <R> Optional<R> get(Config<R> config) {
        String key = config.getKey();
        TypeConverter<R> converter = config.getConverter();
        Optional<byte[]> value = dao.getValue(key);
        return value.isPresent() ? Optional.ofNullable(converter.unpack(value.get())) : Optional.empty();
    }

    protected <R> void set(Config<R> config, R newValue) {
        String key = config.getKey();
        TypeConverter<R> converter = config.getConverter();

        T dbConfig = dao.getDbConfig(key);
        if (dbConfig == null) {
            dbConfig = newInstance();
            dbConfig.setKey(key);
        }

        dbConfig.setValue(converter.pack(newValue));

        dao.makePersistent(dbConfig);
    }

    @Transactional
    public abstract void createConfigIfNotExist();

    private T newInstance() {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException
                | InstantiationException
                | IllegalAccessException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
