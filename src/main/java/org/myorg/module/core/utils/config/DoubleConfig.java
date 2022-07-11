package org.myorg.module.core.utils.config;

import org.myorg.module.core.utils.TypeConvert;

public class DoubleConfig extends Config<Double> {

    public DoubleConfig(String key, Double defaultValue) {
        super(key, defaultValue);
    }

    @Override
    public TypeConverter<Double> getConverter() {
        return new TypeConverter<Double>() {
            @Override
            public byte[] pack(Double value) {
                return TypeConvert.pack(value);
            }

            @Override
            public Double unpack(byte[] value) {
                return TypeConvert.unpackDouble(value);
            }
        };
    }
}
