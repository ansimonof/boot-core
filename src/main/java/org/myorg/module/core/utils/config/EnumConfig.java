package org.myorg.module.core.utils.config;

import org.myorg.module.core.utils.TypeConvert;
import org.myorg.modules.utils.BaseEnum;

public class EnumConfig<T extends Enum<T> & BaseEnum> extends Config<T> {

    private final T[] enumConsts;

    public EnumConfig(String key, T defaultValue, Class<T> type) {
        super(key, defaultValue);
        enumConsts = type.getEnumConstants();
    }

    @Override
    public TypeConverter<T> getConverter() {
        return new TypeConverter<T>() {
            @Override
            public byte[] pack(T value) {
                return value != null ? TypeConvert.pack(value.intValue()) : null;
            }

            @Override
            public T unpack(byte[] value) {
                if (value == null) {
                    return null;
                }

                int id = TypeConvert.unpackInteger(value);
                for (T enumConst : enumConsts) {
                    if (id == enumConst.intValue()) {
                        return enumConst;
                    }
                }

                return null;
            }
        };
    }
}
