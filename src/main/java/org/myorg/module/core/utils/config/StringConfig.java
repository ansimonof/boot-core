package org.myorg.module.core.utils.config;

import org.myorg.module.core.utils.TypeConvert;

public class StringConfig extends Config<String> {

    public StringConfig(String key, String defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public TypeConverter<String> getConverter() {
        return new TypeConverter<String>() {
            @Override
            public byte[] pack(String value) {
                return TypeConvert.pack(value);
            }

            @Override
            public String unpack(byte[] value) {
                return TypeConvert.unpackString(value);
            }
        };
    }
}
