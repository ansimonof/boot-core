package org.myorg.module.core.utils.config;

public abstract class Config<T> {

    private final String key;
    private final T defaultValue;

    Config(String key, T defaultValue) {
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public String getKey() {
        return key;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public abstract TypeConverter<T> getConverter();
}
