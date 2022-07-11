package org.myorg.module.core.utils.config;

import org.myorg.module.core.utils.TypeConvert;

public class LongConfig extends Config<Long> {

    public LongConfig(String key, Long defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public TypeConverter<Long> getConverter() {
        return new TypeConverter<Long>() {
            @Override
            public byte[] pack(Long value) {
                return TypeConvert.pack(value);
            }

            @Override
            public Long unpack(byte[] value) {
                return TypeConvert.unpackLong(value);
            }
        };
    }
}
