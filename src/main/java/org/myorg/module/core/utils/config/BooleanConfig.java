package org.myorg.module.core.utils.config;

import org.myorg.module.core.utils.TypeConvert;

public class BooleanConfig extends Config<Boolean> {

    public BooleanConfig(String key, Boolean defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public TypeConverter<Boolean> getConverter() {
        return new TypeConverter<Boolean>() {
            @Override
            public byte[] pack(Boolean value) {
                return TypeConvert.pack(value);
            }

            @Override
            public Boolean unpack(byte[] value) {
                return TypeConvert.unpackBoolean(value);
            }
        };
    }
}
