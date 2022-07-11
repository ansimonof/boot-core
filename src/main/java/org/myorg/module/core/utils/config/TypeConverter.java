package org.myorg.module.core.utils.config;

public interface TypeConverter<T> {

    byte[] pack(T value);

    T unpack(byte[] value);
}
