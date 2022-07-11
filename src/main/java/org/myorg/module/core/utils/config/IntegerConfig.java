package org.myorg.module.core.utils.config;

import org.myorg.module.core.utils.TypeConvert;

public class IntegerConfig extends Config<Integer> {

    public IntegerConfig(String key, Integer defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public TypeConverter<Integer> getConverter() {
        return new TypeConverter<Integer>() {
            @Override
            public byte[] pack(Integer value) {
                return TypeConvert.pack(value);
            }

            @Override
            public Integer unpack(byte[] value) {
                return TypeConvert.unpackInteger(value);
            }
        };
    }
}
