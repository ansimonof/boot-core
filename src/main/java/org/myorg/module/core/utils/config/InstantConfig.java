package org.myorg.module.core.utils.config;

import org.myorg.module.core.utils.TypeConvert;

import java.time.Instant;

public class InstantConfig extends Config<Instant> {

    public InstantConfig(String key, Instant defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public TypeConverter<Instant> getConverter() {
        return new TypeConverter<Instant>() {
            @Override
            public byte[] pack(Instant value) {
                return TypeConvert.pack(value);
            }

            @Override
            public Instant unpack(byte[] value) {
                return TypeConvert.unpackInstant(value);
            }
        };
    }
}
